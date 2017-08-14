/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.entities.PurchaseOrder;
import org.rainbow.asset.explorer.core.entities.PurchaseOrderDetail;
import org.rainbow.asset.explorer.core.entities.PurchaseOrderDetailId;
import org.rainbow.asset.explorer.core.entities.PurchaseOrderStatus;
import org.rainbow.asset.explorer.core.entities.ShipMethod;
import org.rainbow.asset.explorer.core.entities.Vendor;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicatePurchaseOrderReferenceNumberException;
import org.rainbow.asset.explorer.core.persistence.exceptions.PurchaseOrderCompleteQuantityOutOfRangeException;
import org.rainbow.asset.explorer.core.persistence.exceptions.PurchaseOrderDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.core.persistence.exceptions.PurchaseOrderReadOnlyException;
import org.rainbow.asset.explorer.core.persistence.exceptions.PurchaseOrderStatusTransitionException;
import org.rainbow.asset.explorer.core.persistence.exceptions.PurchaseOrderVendorException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.ListValuedFilter;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.UpdateOperation;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class PurchaseOrderDaoImpl extends TrackableDaoImpl<PurchaseOrder, Long> implements PurchaseOrderDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("inventoryManager")
	private InventoryManager inventoryManager;

	@Autowired
	@Qualifier("productDao")
	private Dao<Product, Long, SearchOptions> productDao;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public PurchaseOrderDaoImpl() {
		super(PurchaseOrder.class);
	}

	private void validateVendor(PurchaseOrder purchaseOrder) throws Exception {
		if (purchaseOrder.getVendor() != null && purchaseOrder.getVendor().getId() != null) {
			Vendor persistentVendor = new EntityManagerHelper(em).find(Vendor.class, purchaseOrder.getVendor());

			if (!persistentVendor.isActive() && purchaseOrder.getStatus() != PurchaseOrderStatus.REJECTED) {
				throw new PurchaseOrderVendorException();
			}
			purchaseOrder.setVendor(persistentVendor);
		}
	}

	private void validatePersistentStatus(PurchaseOrderStatus status, UpdateOperation operation)
			throws PurchaseOrderReadOnlyException {
		if (operation == UpdateOperation.UPDATE || operation == UpdateOperation.DELETE) {
			if (status != null && status != PurchaseOrderStatus.PENDING) {
				throw new PurchaseOrderReadOnlyException(status);
			}
		}
	}

	private void validateTransition(PurchaseOrderStatus oldStatus, PurchaseOrderStatus newStatus)
			throws PurchaseOrderStatusTransitionException {
		if (oldStatus == PurchaseOrderStatus.PENDING) {
			if (newStatus == PurchaseOrderStatus.PENDING || newStatus == PurchaseOrderStatus.APPROVED
					|| newStatus == PurchaseOrderStatus.REJECTED) {
				return;
			}
		} else if (oldStatus == PurchaseOrderStatus.APPROVED) {
			if (newStatus == PurchaseOrderStatus.COMPLETE) {
				return;
			}
		}
		throw new PurchaseOrderStatusTransitionException(oldStatus, newStatus);
	}

	private PurchaseOrderStatus getPersistentStatus(Long purchaseOrderId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<PurchaseOrder> rt = cq.from(PurchaseOrder.class);
		cq.select(cb.tuple(rt.get("status")));
		cq.where(cb.equal(rt.get("id"), purchaseOrderId));
		TypedQuery<Tuple> tq = em.createQuery(cq);
		List<Tuple> results = tq.getResultList();
		if (!results.isEmpty()) {
			return (PurchaseOrderStatus) results.get(0).get(0);
		}
		return null;
	}

	@Override
	protected void validate(PurchaseOrder purchaseOrder, UpdateOperation operation) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE: {
			id = purchaseOrder.getId();
			if (helper.isDuplicate(PurchaseOrder.class, purchaseOrder.getReferenceNumber(), "referenceNumber", "id",
					id)) {
				throw new DuplicatePurchaseOrderReferenceNumberException(purchaseOrder.getReferenceNumber());
			}
			validateVendor(purchaseOrder);
			if (operation == UpdateOperation.UPDATE) {
				PurchaseOrderStatus oldStatus = getPersistentStatus(purchaseOrder.getId());
				validatePersistentStatus(oldStatus, operation); // If no
																// exception is
																// thrown at
																// this point,
																// then the old
																// status is
																// PENDING.
			}
			break;
		}
		case DELETE: {
			PurchaseOrderStatus oldStatus = getPersistentStatus(purchaseOrder.getId());
			validatePersistentStatus(oldStatus, operation);
			break;
		}
		}
	}

	@Override
	public void create(PurchaseOrder purchaseOrder) throws Exception {
		purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);

		ShipMethod shipMethod = purchaseOrder.getShipMethod();
		if (shipMethod != null) {
			purchaseOrder.setShipMethod(new EntityManagerHelper(em).find(ShipMethod.class, shipMethod));
		}

		List<PurchaseOrderDetail> details = purchaseOrder.getDetails();
		if (details == null || details.isEmpty()) {
			throw new PurchaseOrderDetailsNullOrEmptyException();
		}
		List<Long> productIds = new ArrayList<>();
		for (PurchaseOrderDetail detail : details) {
			productIds.add(detail.getProduct().getId());
		}
		List<Product> persistentProducts = getPersistentProducts(productIds);

		int detailId = 0;
		int noOfDetailsWithPositiveQuantities = 0;

		// We validate the Purchase Order
		validate(purchaseOrder, UpdateOperation.CREATE);
		// We persist and flush here because we need the Id of the Purchase
		// Order so that we can set it in Purchase Order details.
		em.persist(purchaseOrder);
		em.flush();

		for (PurchaseOrderDetail detail : details) {
			detail.setCreator(purchaseOrder.getCreator());
			detail.setUpdater(purchaseOrder.getUpdater());

			if (!persistentProducts.contains(detail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", detail.getProduct().getId()));
			}
			detail.setProduct(persistentProducts.get(persistentProducts.indexOf(detail.getProduct())));

			if (detail.getOrderedQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;

				PurchaseOrderDetailId purchaseOrderDetailId = detail.getId();
				if (purchaseOrderDetailId == null) {
					purchaseOrderDetailId = new PurchaseOrderDetailId(purchaseOrder.getId(), ++detailId);
					detail.setId(purchaseOrderDetailId);
				} else {
					purchaseOrderDetailId.setDetailId(++detailId);
				}

				detail.setReceivedQuantity((short) 0);
				detail.setRejectedQuantity((short) 0);

				em.persist(detail);
			}
		}
		if (noOfDetailsWithPositiveQuantities == 0) {
			throw new PurchaseOrderDetailsNullOrEmptyException();
		}
	}

	private int getDetailId(List<PurchaseOrderDetail> oldDetails, List<PurchaseOrderDetail> newDetails) {
		List<Integer> detailIds = new ArrayList<>();
		for (PurchaseOrderDetail oldDetail : oldDetails) {
			Integer detailId = oldDetail.getId().getDetailId();
			if (!detailIds.contains(detailId)) {
				detailIds.add(detailId);
			}
		}
		for (PurchaseOrderDetail newDetail : newDetails) {
			Integer detailId = newDetail.getId().getDetailId();
			if (!detailIds.contains(detailId)) {
				detailIds.add(detailId);
			}
		}
		int detailId = 0;
		while (detailId++ < newDetails.size()) {
			if (!detailIds.contains(detailId)) {
				break;
			}
		}
		return detailId;
	}

	private List<Product> getPersistentProducts(List<Long> productIds) throws Exception {
		SearchOptions options = new SearchOptions();

		ListValuedFilter<Long> filter = new ListValuedFilter<>("id");
		filter.setOperator(RelationalOperator.IN);
		filter.setList(productIds);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);

		options.setFilters(filters);

		return productDao.find(options);
	}

	@Override
	public void update(PurchaseOrder purchaseOrder) throws Exception {
		// We set the status of the purchase order to PENDING. This is because
		// we do not want any status change to happen in the edit method.
		purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
		ShipMethod shipMethod = purchaseOrder.getShipMethod();
		if (shipMethod != null) {
			purchaseOrder.setShipMethod(new EntityManagerHelper(em).find(ShipMethod.class, shipMethod));
		}

		List<PurchaseOrderDetail> newDetails = purchaseOrder.getDetails();
		if (newDetails == null || newDetails.isEmpty()) {
			throw new PurchaseOrderDetailsNullOrEmptyException();
		}
		List<PurchaseOrderDetail> oldDetails = getDetails(purchaseOrder.getId());

		List<Long> productIds = new ArrayList<>();
		for (PurchaseOrderDetail detail : newDetails) {
			productIds.add(detail.getProduct().getId());
		}
		List<Product> persistentProducts = getPersistentProducts(productIds);

		int noOfDetailsWithPositiveQuantities = 0;
		for (PurchaseOrderDetail newDetail : newDetails) {
			newDetail.setUpdater(purchaseOrder.getUpdater());

			if (!persistentProducts.contains(newDetail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", newDetail.getProduct().getId()));
			}
			newDetail.setProduct(persistentProducts.get(persistentProducts.indexOf(newDetail.getProduct())));

			newDetail.setReceivedQuantity((short) 0);
			newDetail.setRejectedQuantity((short) 0);

			if (!oldDetails.contains(newDetail)) {
				PurchaseOrderDetailId purchaseOrderDetailId = newDetail.getId();
				if (purchaseOrderDetailId == null) {
					purchaseOrderDetailId = new PurchaseOrderDetailId(purchaseOrder.getId(),
							getDetailId(oldDetails, newDetails));
					newDetail.setId(purchaseOrderDetailId);
				} else {
					purchaseOrderDetailId.setDetailId(getDetailId(oldDetails, newDetails));
				}

				em.persist(newDetail);
			} else {
				em.merge(newDetail);
			}
			if (newDetail.getOrderedQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;
			}
		}

		if (noOfDetailsWithPositiveQuantities == 0) {
			throw new PurchaseOrderDetailsNullOrEmptyException();
		}

		for (PurchaseOrderDetail oldDetail : oldDetails) {
			if (!newDetails.contains(oldDetail)) {
				em.remove(oldDetail);
			}
		}
		super.update(purchaseOrder);
	}

	private PurchaseOrder changeStatus(PurchaseOrder purchaseOrder, PurchaseOrderStatus newStatus) throws Exception {
		PurchaseOrderStatus oldStatus = purchaseOrder.getStatus();
		validateTransition(oldStatus, newStatus);
		purchaseOrder.setStatus(newStatus);
		validateVendor(purchaseOrder);
		em.merge(purchaseOrder);
		return purchaseOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.PurchaseOrderDao#approve(
	 * org.rainbow.asset.explorer.core.entities.PurchaseOrder)
	 */
	@Override
	public void approve(PurchaseOrder purchaseOrder) throws Exception {
		PurchaseOrder persistentPurchaseOrder = getPersistent(purchaseOrder);
		persistentPurchaseOrder.setUpdater(purchaseOrder.getUpdater());
		changeStatus(persistentPurchaseOrder, PurchaseOrderStatus.APPROVED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.PurchaseOrderDao#reject(
	 * org.rainbow.asset.explorer.core.entities.PurchaseOrder)
	 */
	@Override
	public void reject(PurchaseOrder purchaseOrder) throws Exception {
		PurchaseOrder persistentPurchaseOrder = getPersistent(purchaseOrder);
		persistentPurchaseOrder.setUpdater(purchaseOrder.getUpdater());
		changeStatus(persistentPurchaseOrder, PurchaseOrderStatus.REJECTED);
	}

	private void validateComplete(PurchaseOrder purchaseOrder, Map<Long, Short> productsCount)
			throws PurchaseOrderCompleteQuantityOutOfRangeException {
		List<Long> productIds = new ArrayList<>(productsCount.keySet());
		List<PurchaseOrderDetail> details = purchaseOrder.getDetails();
		for (Long productId : productIds) {
			short receivedQuantity = productsCount.get(productId);
			short orderedQuantity = 0;
			for (PurchaseOrderDetail detail : details) {
				if (detail.getProduct().getId().equals(productId)) {
					orderedQuantity += detail.getOrderedQuantity();
				}
			}
			if (receivedQuantity < 0 || receivedQuantity > orderedQuantity) {
				throw new PurchaseOrderCompleteQuantityOutOfRangeException(purchaseOrder.getId(), productId,
						orderedQuantity, receivedQuantity);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.PurchaseOrderDao#complete
	 * (org.rainbow.asset.explorer.core.entities.PurchaseOrder,
	 * org.rainbow.asset.explorer.core.entities.Location, java.util.Map)
	 */
	@Override
	public void complete(PurchaseOrder purchaseOrder, Location location, Map<Long, Short> productsCount)
			throws Exception {
		PurchaseOrder persistentPurchaseOrder = getPersistent(purchaseOrder);
		persistentPurchaseOrder.setUpdater(purchaseOrder.getUpdater());

		Location persistentLocation = new EntityManagerHelper(em).find(Location.class, location);
		changeStatus(persistentPurchaseOrder, PurchaseOrderStatus.COMPLETE);
		List<PurchaseOrderDetail> details = getDetails(persistentPurchaseOrder.getId());
		persistentPurchaseOrder.setDetails(details);
		validateComplete(persistentPurchaseOrder, productsCount);
		persistentPurchaseOrder.setLocation(persistentLocation);
		em.merge(persistentPurchaseOrder);

		for (PurchaseOrderDetail detail : details) {
			detail.setUpdater(purchaseOrder.getUpdater());

			Long productId = detail.getProduct().getId();
			short receivedQuantity = 0;
			if (productsCount.containsKey(productId)) {
				receivedQuantity = productsCount.get(productId);
			}
			detail.setReceivedQuantity(receivedQuantity);
			detail.setRejectedQuantity((short) (detail.getOrderedQuantity() - receivedQuantity));
			em.merge(detail);
		}
		inventoryManager.add(persistentLocation.getId(), productsCount);
	}

	@Override
	public void delete(PurchaseOrder entity) throws Exception {
		super.delete(entity);
		List<PurchaseOrderDetail> details = getDetails(entity.getId());
		for (PurchaseOrderDetail detail : details) {
			detail.setUpdater(entity.getUpdater());
			detail.setLastUpdateDate(new Date());

			em.remove(detail);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.asset.explorer.core.persistence.dao.PurchaseOrderDao#
	 * getDetails(java.lang.Long)
	 */
	@Override
	public List<PurchaseOrderDetail> getDetails(Long purchaseOrderId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PurchaseOrderDetail> cq = cb.createQuery(PurchaseOrderDetail.class);
		Root<PurchaseOrderDetail> rt = cq.from(PurchaseOrderDetail.class);

		Expression<Boolean> exp = cb.equal(rt.get("id").get("purchaseOrderId"), purchaseOrderId);
		cq.where(exp);

		TypedQuery<PurchaseOrderDetail> query = em.createQuery(cq);

		return query.getResultList();
	}
}
