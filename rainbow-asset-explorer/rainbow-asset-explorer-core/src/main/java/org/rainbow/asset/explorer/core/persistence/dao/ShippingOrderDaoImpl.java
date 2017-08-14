/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
import org.rainbow.asset.explorer.core.entities.ShipMethod;
import org.rainbow.asset.explorer.core.entities.ShippingOrder;
import org.rainbow.asset.explorer.core.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.core.entities.ShippingOrderDetailId;
import org.rainbow.asset.explorer.core.entities.ShippingOrderStatus;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateShippingOrderReferenceNumberException;
import org.rainbow.asset.explorer.core.persistence.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderDeliveredQuantityOutOfRangeException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderLocationException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderReadOnlyException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderStatusTransitionException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
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
public class ShippingOrderDaoImpl extends TrackableDaoImpl<ShippingOrder, Long> implements ShippingOrderDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("productDao")
	private ProductDao productDao;

	@Autowired
	@Qualifier("inventoryManager")
	private InventoryManager inventoryManager;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ShippingOrderDaoImpl() {
		super(ShippingOrder.class);
	}

	private void validatePersistentStatus(ShippingOrderStatus status, UpdateOperation operation)
			throws ShippingOrderReadOnlyException {
		if (operation == UpdateOperation.UPDATE || operation == UpdateOperation.DELETE) {
			if (status != null && status != ShippingOrderStatus.PENDING) {
				throw new ShippingOrderReadOnlyException(status);
			}
		}
	}

	private void validateTransition(ShippingOrderStatus oldStatus, ShippingOrderStatus newStatus)
			throws ShippingOrderStatusTransitionException {
		if (null != oldStatus) {
			switch (oldStatus) {
			case PENDING:
				if (newStatus == ShippingOrderStatus.PENDING || newStatus == ShippingOrderStatus.APPROVED
						|| newStatus == ShippingOrderStatus.REJECTED) {
					return;
				}
				break;
			case APPROVED:
				if (newStatus == ShippingOrderStatus.IN_TRANSIT) {
					return;
				}
				break;
			case IN_TRANSIT:
				if (newStatus == ShippingOrderStatus.DELIVERED || newStatus == ShippingOrderStatus.RESTITUTED) {
					return;
				}
				break;
			default:
				break;
			}
		}
		throw new ShippingOrderStatusTransitionException(oldStatus, newStatus);
	}

	private ShippingOrderStatus getPersistentStatus(Long shippingOrderId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<ShippingOrder> rt = cq.from(ShippingOrder.class);
		cq.select(cb.tuple(rt.get("status")));
		cq.where(cb.equal(rt.get("id"), shippingOrderId));
		TypedQuery<Tuple> tq = em.createQuery(cq);
		List<Tuple> results = tq.getResultList();
		if (!results.isEmpty()) {
			return (ShippingOrderStatus) results.get(0).get(0);
		}
		return null;
	}

	@Override
	protected void validate(ShippingOrder shippingOrder, UpdateOperation operation) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE: {
			id = shippingOrder.getId();
			if (helper.isDuplicate(ShippingOrder.class, shippingOrder.getReferenceNumber(), "referenceNumber", "id",
					id)) {
				throw new DuplicateShippingOrderReferenceNumberException(shippingOrder.getReferenceNumber());
			}
			validateLocations(shippingOrder);
			if (operation == UpdateOperation.UPDATE) {
				ShippingOrderStatus oldStatus = getPersistentStatus(id);
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
			ShippingOrderStatus oldStatus = getPersistentStatus(shippingOrder.getId());
			validatePersistentStatus(oldStatus, operation);
			break;
		}
		}
	}

	private void validateLocations(ShippingOrder shippingOrder) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		shippingOrder.setSourceLocation(helper.find(Location.class, shippingOrder.getSourceLocation()));
		shippingOrder.setTargetLocation(helper.find(Location.class, shippingOrder.getTargetLocation()));
		if (shippingOrder.getSourceLocation().equals(shippingOrder.getTargetLocation())) {
			throw new ShippingOrderLocationException(shippingOrder.getSourceLocation().getId());
		}
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
	public void create(ShippingOrder shippingOrder) throws Exception {
		shippingOrder.setStatus(ShippingOrderStatus.PENDING);
		shippingOrder.setShipDate(null);
		shippingOrder.setShipMethod(new EntityManagerHelper(em).find(ShipMethod.class, shippingOrder.getShipMethod()));

		List<ShippingOrderDetail> details = shippingOrder.getDetails();
		if (details == null || details.isEmpty()) {
			throw new ShippingOrderDetailsNullOrEmptyException();
		}

		List<Long> productIds = new ArrayList<>();
		for (ShippingOrderDetail detail : details) {
			productIds.add(detail.getProduct().getId());
		}
		List<Product> persistentProducts = getPersistentProducts(productIds);

		int detailId = 0;
		int noOfDetailsWithPositiveQuantities = 0;

		// We validate the Shipping Order
		validate(shippingOrder, UpdateOperation.CREATE);
		// We persist and flush here because we need the Id of the Shipping
		// Order so that we can set it in Shipping Order details.
		em.persist(shippingOrder);
		em.flush();

		for (ShippingOrderDetail detail : details) {
			detail.setCreator(shippingOrder.getCreator());
			detail.setUpdater(shippingOrder.getUpdater());

			if (!persistentProducts.contains(detail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", detail.getProduct().getId()));
			}
			detail.setProduct(persistentProducts.get(persistentProducts.indexOf(detail.getProduct())));

			if (detail.getShippedQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;

				ShippingOrderDetailId shippingOrderDetailId = detail.getId();
				if (shippingOrderDetailId == null) {
					shippingOrderDetailId = new ShippingOrderDetailId(shippingOrder.getId(), ++detailId);
					detail.setId(shippingOrderDetailId);
				} else {
					shippingOrderDetailId.setDetailId(++detailId);
				}

				detail.setReceivedQuantity((short) 0);
				detail.setRejectedQuantity((short) 0);

				em.persist(detail);
			}
		}
		if (noOfDetailsWithPositiveQuantities == 0) {
			throw new ShippingOrderDetailsNullOrEmptyException();
		}
	}

	private int getDetailId(List<ShippingOrderDetail> oldDetails, List<ShippingOrderDetail> newDetails) {
		List<Integer> detailIds = new ArrayList<>();
		for (ShippingOrderDetail oldDetail : oldDetails) {
			Integer detailId = oldDetail.getId().getDetailId();
			if (!detailIds.contains(detailId)) {
				detailIds.add(detailId);
			}
		}
		for (ShippingOrderDetail newDetail : newDetails) {
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

	@Override
	public void update(ShippingOrder shippingOrder) throws Exception {
		// We set the status of the purchase order to PENDING. This is because
		// we do not want any status change to happen in the edit method.
		shippingOrder.setStatus(ShippingOrderStatus.PENDING);
		shippingOrder.setShipDate(null);
		shippingOrder.setShipMethod(new EntityManagerHelper(em).find(ShipMethod.class, shippingOrder.getShipMethod()));

		List<ShippingOrderDetail> newDetails = shippingOrder.getDetails();
		if (newDetails == null || newDetails.isEmpty()) {
			throw new ShippingOrderDetailsNullOrEmptyException();
		}

		List<ShippingOrderDetail> oldDetails = getDetails(shippingOrder.getId());

		List<Long> productIds = new ArrayList<>();
		for (ShippingOrderDetail detail : newDetails) {
			productIds.add(detail.getProduct().getId());
		}
		List<Product> persistentProducts = getPersistentProducts(productIds);

		int noOfDetailsWithPositiveQuantities = 0;
		for (ShippingOrderDetail newDetail : newDetails) {
			newDetail.setUpdater(shippingOrder.getUpdater());

			if (!persistentProducts.contains(newDetail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", newDetail.getProduct().getId()));
			}
			newDetail.setProduct(persistentProducts.get(persistentProducts.indexOf(newDetail.getProduct())));

			newDetail.setReceivedQuantity((short) 0);
			newDetail.setRejectedQuantity((short) 0);
			if (!oldDetails.contains(newDetail)) {
				ShippingOrderDetailId shippingOrderDetailId = newDetail.getId();
				if (shippingOrderDetailId == null) {
					shippingOrderDetailId = new ShippingOrderDetailId(shippingOrder.getId(),
							getDetailId(oldDetails, newDetails));
					newDetail.setId(shippingOrderDetailId);
				} else {
					shippingOrderDetailId.setDetailId(getDetailId(oldDetails, newDetails));
				}

				em.persist(newDetail);
			} else {
				em.merge(newDetail);
			}
			if (newDetail.getShippedQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;
			}
		}

		if (noOfDetailsWithPositiveQuantities == 0) {
			throw new ShippingOrderDetailsNullOrEmptyException();
		}

		for (ShippingOrderDetail oldDetail : oldDetails) {
			if (!newDetails.contains(oldDetail)) {
				em.remove(oldDetail);
			}
		}
		super.update(shippingOrder);
	}

	private ShippingOrder changeStatus(ShippingOrder shippingOrder, ShippingOrderStatus newStatus)
			throws ShippingOrderStatusTransitionException, NonexistentEntityException {
		ShippingOrderStatus oldStatus = shippingOrder.getStatus();
		validateTransition(oldStatus, newStatus);
		shippingOrder.setStatus(newStatus);
		em.merge(shippingOrder);
		return shippingOrder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.ShippingOrderDao#approve(
	 * org.rainbow.asset.explorer.core.entities.ShippingOrder)
	 */
	@Override
	public void approve(ShippingOrder shippingOrder) throws Exception {
		ShippingOrder persistentShippingOrder = getPersistent(shippingOrder);
		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.APPROVED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.ShippingOrderDao#reject(
	 * org.rainbow.asset.explorer.core.entities.ShippingOrder)
	 */
	@Override
	public void reject(ShippingOrder shippingOrder) throws Exception {
		ShippingOrder persistentShippingOrder = getPersistent(shippingOrder);
		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.REJECTED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.ShippingOrderDao#transit(
	 * org.rainbow.asset.explorer.core.entities.ShippingOrder)
	 */
	@Override
	public void transit(ShippingOrder shippingOrder)
			throws ShippingOrderStatusTransitionException, InsufficientInventoryException, NonexistentEntityException {
		ShippingOrder persistentShippingOrder = getPersistent(shippingOrder);
		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.IN_TRANSIT);
		persistentShippingOrder.setShipDate(new Date());

		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		Map<Long, Short> productsCount = new HashMap<>();
		for (ShippingOrderDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!productsCount.containsKey(productId)) {
				productsCount.put(productId, (short) 0);
			}
			short shippedQuantity = detail.getShippedQuantity();
			if (shippedQuantity > 0) {
				productsCount.replace(productId, (short) (productsCount.get(productId) + shippedQuantity));
			}
		}

		inventoryManager.substract(persistentShippingOrder.getSourceLocation().getId(), productsCount);
	}

	private void validateDelivery(ShippingOrder shippingOrder, Map<Long, Short> productsCount)
			throws ShippingOrderDeliveredQuantityOutOfRangeException {
		List<Long> productIds = new ArrayList<>(productsCount.keySet());
		List<ShippingOrderDetail> details = shippingOrder.getDetails();
		for (Long productId : productIds) {
			Short deliveredQuantity = productsCount.get(productId);
			short shippedQuantity = 0;
			for (ShippingOrderDetail detail : details) {
				if (detail.getProduct().getId().equals(productId)) {
					shippedQuantity += detail.getShippedQuantity();
				}
			}
			if (deliveredQuantity < 0 || deliveredQuantity > shippedQuantity) {
				throw new ShippingOrderDeliveredQuantityOutOfRangeException(shippingOrder.getId(), productId,
						shippedQuantity, deliveredQuantity);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.ShippingOrderDao#deliver(
	 * org.rainbow.asset.explorer.core.entities.ShippingOrder, java.util.Map)
	 */
	@Override
	public void deliver(ShippingOrder shippingOrder, Map<Long, Short> productsCount)
			throws ShippingOrderStatusTransitionException, ShippingOrderDeliveredQuantityOutOfRangeException,
			NonexistentEntityException {
		ShippingOrder persistentShippingOrder = getPersistent(shippingOrder);
		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.DELIVERED);
		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		persistentShippingOrder.setDetails(details);

		validateDelivery(persistentShippingOrder, productsCount);

		persistentShippingOrder.setDeliveryDate(new Date());

		for (ShippingOrderDetail detail : details) {
			detail.setUpdater(shippingOrder.getUpdater());

			Long productId = detail.getProduct().getId();
			short deliveredQuantity = 0;
			if (productsCount.containsKey(productId)) {
				deliveredQuantity = productsCount.get(productId);
			}
			detail.setReceivedQuantity(deliveredQuantity);
			detail.setRejectedQuantity((short) (detail.getShippedQuantity() - deliveredQuantity));
			em.merge(detail);
		}

		inventoryManager.add(persistentShippingOrder.getTargetLocation().getId(), productsCount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.asset.explorer.core.persistence.dao.ShippingOrderDao#
	 * restitute(org.rainbow.asset.explorer.core.entities.ShippingOrder)
	 */
	@Override
	public void restitute(ShippingOrder shippingOrder)
			throws ShippingOrderStatusTransitionException, NonexistentEntityException {
		ShippingOrder persistentShippingOrder = getPersistent(shippingOrder);
		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.RESTITUTED);

		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		Map<Long, Short> productsCount = new HashMap<>();
		for (ShippingOrderDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!productsCount.containsKey(productId)) {
				productsCount.put(productId, (short) 0);
			}
			short shippedQuantity = detail.getShippedQuantity();
			if (shippedQuantity > 0) {
				productsCount.replace(productId, (short) (productsCount.get(productId) + shippedQuantity));
			}
		}

		inventoryManager.add(persistentShippingOrder.getSourceLocation().getId(), productsCount);
	}

	@Override
	public void delete(ShippingOrder shippingOrder) throws Exception {
		super.delete(shippingOrder);
		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		for (ShippingOrderDetail detail : details) {
			detail.setUpdater(shippingOrder.getUpdater());
			detail.setLastUpdateDate(new Date());

			em.remove(detail);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.asset.explorer.core.persistence.dao.ShippingOrderDao#
	 * getDetails(java.lang.Long)
	 */
	@Override
	public List<ShippingOrderDetail> getDetails(Long shippingOrderId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ShippingOrderDetail> cq = cb.createQuery(ShippingOrderDetail.class);
		Root<ShippingOrderDetail> rt = cq.from(ShippingOrderDetail.class);

		Expression<Boolean> exp = cb.equal(rt.get("id").get("shippingOrderId"), shippingOrderId);
		cq.where(exp);

		TypedQuery<ShippingOrderDetail> query = em.createQuery(cq);

		return query.getResultList();
	}
}
