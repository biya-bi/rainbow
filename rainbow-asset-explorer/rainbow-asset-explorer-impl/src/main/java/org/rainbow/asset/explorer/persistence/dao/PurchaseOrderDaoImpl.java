package org.rainbow.asset.explorer.persistence.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrder;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetail;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetailId;
import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.Pageable;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.utilities.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class PurchaseOrderDaoImpl extends TrackableDaoImpl<PurchaseOrder, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public PurchaseOrderDaoImpl() {
		super(PurchaseOrder.class);
	}

	@Override
	protected void onCreate(PurchaseOrder purchaseOrder) throws Exception {
		fixAssociations(purchaseOrder);
		super.onCreate(purchaseOrder);
	}

	@Override
	protected void onUpdate(PurchaseOrder purchaseOrder) throws Exception {
		fixAssociations(purchaseOrder);
		super.onUpdate(purchaseOrder);
	}

	private List<Product> getProducts(List<Long> ids) {
		CriteriaBuilder cb = this.getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Product> cq = cb.createQuery(Product.class);
		Root<Product> rt = cq.from(Product.class);

		Expression<Boolean> exp = rt.get("id").in(ids);

		cq = cq.select(rt).where(exp);

		TypedQuery<Product> query = this.getEntityManager().createQuery(cq);
		return query.getResultList();
	}

	@Override
	protected void onDelete(PurchaseOrder purchaseOrder) throws Exception {
		List<PurchaseOrderDetail> details = getDetails(purchaseOrder.getId());
		for (PurchaseOrderDetail detail : details) {
			detail.setUpdater(purchaseOrder.getUpdater());
			detail.setLastUpdateDate(new Date());
		}
		purchaseOrder.setDetails(details);
		super.onDelete(purchaseOrder);
	}

	private List<PurchaseOrderDetail> getDetails(Long purchaseOrderId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PurchaseOrderDetail> cq = cb.createQuery(PurchaseOrderDetail.class);
		Root<PurchaseOrderDetail> rt = cq.from(PurchaseOrderDetail.class);

		Expression<Boolean> exp = cb.equal(rt.join("purchaseOrder").get("id"), purchaseOrderId);
		cq.where(exp);

		TypedQuery<PurchaseOrderDetail> query = em.createQuery(cq);

		return query.getResultList();
	}

	private void fixAssociations(PurchaseOrder purchaseOrder) {
		List<Integer> usedDetailIds = null;
		List<PurchaseOrderDetail> oldDetails = null;

		final List<PurchaseOrderDetail> currentDetails = purchaseOrder.getDetails();

		if (purchaseOrder.getId() != null) {
			oldDetails = getDetails(purchaseOrder.getId());
			usedDetailIds = oldDetails.stream().map(x -> x.getId().getDetailId()).collect(Collectors.toList());
		} else {
			usedDetailIds = new ArrayList<>();
		}

		if (currentDetails != null) {
			final List<Product> products = getProducts(
					currentDetails.stream().map(x -> x.getProduct().getId()).collect(Collectors.toList()));

			for (PurchaseOrderDetail detail : currentDetails) {
				detail.setPurchaseOrder(purchaseOrder);
				detail.setCreator(purchaseOrder.getCreator());
				detail.setUpdater(purchaseOrder.getUpdater());
				detail.setCreationDate(purchaseOrder.getCreationDate());
				detail.setLastUpdateDate(purchaseOrder.getLastUpdateDate());

				setProduct(detail, products);

				if (oldDetails == null || !oldDetails.contains(detail)) {
					setDetailId(detail, usedDetailIds);

					em.persist(detail);
				} else {
					em.merge(detail);
				}
			}
		}

		if (oldDetails != null) {

			for (PurchaseOrderDetail detail : oldDetails) {
				if (currentDetails != null) {
					if (!currentDetails.contains(detail)) {
						detail.setUpdater(purchaseOrder.getUpdater());
						detail.setCreationDate(purchaseOrder.getCreationDate());
						detail.setLastUpdateDate(purchaseOrder.getLastUpdateDate());

						em.remove(detail);
					}
				} else {
					em.remove(detail);
				}
			}
		}

		final Vendor vendor = purchaseOrder.getVendor();
		if (vendor != null && vendor.getId() != null) {
			purchaseOrder.setVendor(EntityManagerUtil.findById(this.getEntityManager(), Vendor.class, vendor.getId()));
		}
		final ShipMethod shipMethod = purchaseOrder.getShipMethod();
		if (shipMethod != null) {
			purchaseOrder.setShipMethod(EntityManagerUtil.find(this.getEntityManager(), ShipMethod.class, shipMethod));
		}
	}

	private void setProduct(final PurchaseOrderDetail detail, final List<Product> products) {
		final Product product = detail.getProduct();
		final Optional<Product> optional = products.stream().filter(x -> x.equals(product)).findFirst();
		if (optional.isPresent()) {
			detail.setProduct(optional.get());
		} else {
			throw new NonexistentEntityException(
					String.format("No %s with ID '%s' was found.", Product.class.getSimpleName(), product.getId()));
		}
	}

	private void setDetailId(final PurchaseOrderDetail detail, final List<Integer> usedDetailIds) {
		final int detailId = NumberHelper.getLeastAvailable(usedDetailIds);
		usedDetailIds.add(detailId);

		final PurchaseOrderDetailId id = new PurchaseOrderDetailId();
		id.setDetailId(detailId);
		detail.setId(id);
	}

}
