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

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderDetailId;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.util.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ShippingOrderDaoImpl extends TrackableDaoImpl<ShippingOrder> implements ShippingOrderDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ShippingOrderDaoImpl() {
		super(ShippingOrder.class);
	}

	@Override
	protected void onCreate(ShippingOrder shippingOrder) throws Exception {
		fixAssociations(shippingOrder);
		super.onCreate(shippingOrder);
	}

	@Override
	protected void onUpdate(ShippingOrder shippingOrder) throws Exception {
		fixAssociations(shippingOrder);
		super.onUpdate(shippingOrder);
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

	private void fixAssociations(ShippingOrder shippingOrder) {
		List<Integer> usedDetailIds = null;
		List<ShippingOrderDetail> oldDetails = null;

		final List<ShippingOrderDetail> currentDetails = shippingOrder.getDetails();

		if (shippingOrder.getId() != null) {
			oldDetails = getDetails(shippingOrder.getId());
			usedDetailIds = oldDetails.stream().map(x -> x.getId().getDetailId()).collect(Collectors.toList());
		} else {
			usedDetailIds = new ArrayList<>();
		}

		if (currentDetails != null) {
			final List<Product> products = getProducts(
					currentDetails.stream().map(x -> x.getProduct().getId()).collect(Collectors.toList()));

			for (ShippingOrderDetail detail : currentDetails) {
				detail.setShippingOrder(shippingOrder);
				detail.setCreator(shippingOrder.getCreator());
				detail.setUpdater(shippingOrder.getUpdater());
				detail.setCreationDate(shippingOrder.getCreationDate());
				detail.setLastUpdateDate(shippingOrder.getLastUpdateDate());

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

			for (ShippingOrderDetail detail : oldDetails) {
				if (currentDetails != null) {
					if (!currentDetails.contains(detail)) {
						detail.setUpdater(shippingOrder.getUpdater());
						detail.setCreationDate(shippingOrder.getCreationDate());
						detail.setLastUpdateDate(shippingOrder.getLastUpdateDate());

						em.remove(detail);
					}
				} else {
					em.remove(detail);
				}
			}
		}

		if (shippingOrder.getShipMethod() != null) {
			shippingOrder.setShipMethod(
					EntityManagerUtil.find(this.getEntityManager(), ShipMethod.class, shippingOrder.getShipMethod()));
		}
		if (shippingOrder.getSourceLocation() != null) {
			shippingOrder.setSourceLocation(
					EntityManagerUtil.find(this.getEntityManager(), Location.class, shippingOrder.getSourceLocation()));
		}
		if (shippingOrder.getTargetLocation() != null) {
			shippingOrder.setTargetLocation(
					EntityManagerUtil.find(this.getEntityManager(), Location.class, shippingOrder.getTargetLocation()));
		}
	}

	@Override
	protected void onDelete(ShippingOrder shippingOrder) throws Exception {
		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		for (ShippingOrderDetail detail : details) {
			detail.setUpdater(shippingOrder.getUpdater());
			detail.setLastUpdateDate(new Date());
		}
		shippingOrder.setDetails(details);
		super.onDelete(shippingOrder);
	}

	private List<ShippingOrderDetail> getDetails(Long shippingOrderId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ShippingOrderDetail> cq = cb.createQuery(ShippingOrderDetail.class);
		Root<ShippingOrderDetail> rt = cq.from(ShippingOrderDetail.class);

		Expression<Boolean> exp = cb.equal(rt.join("shippingOrder").get("id"), shippingOrderId);
		cq.where(exp);

		TypedQuery<ShippingOrderDetail> query = em.createQuery(cq);

		return query.getResultList();
	}

	private void setProduct(final ShippingOrderDetail detail, final List<Product> products) {
		final Product product = detail.getProduct();
		final Optional<Product> optional = products.stream().filter(x -> x.equals(product)).findFirst();
		if (optional.isPresent()) {
			detail.setProduct(optional.get());
		} else {
			throw new NonexistentEntityException(
					String.format("No %s with ID '%s' was found.", Product.class.getSimpleName(), product.getId()));
		}
	}

	private void setDetailId(final ShippingOrderDetail detail, final List<Integer> usedDetailIds) {
		final int detailId = NumberHelper.getLeastAvailable(usedDetailIds);
		usedDetailIds.add(detailId);

		final ShippingOrderDetailId id = new ShippingOrderDetailId();
		id.setDetailId(detailId);
		detail.setId(id);
	}

}
