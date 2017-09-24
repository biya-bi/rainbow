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

import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductReceipt;
import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetail;
import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetailId;
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
public class ProductReceiptDaoImpl extends TrackableDaoImpl<ProductReceipt, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProductReceiptDaoImpl() {
		super(ProductReceipt.class);
	}

	@Override
	protected void onCreate(ProductReceipt productReceipt) throws Exception {
		fixAssociations(productReceipt);
		super.onCreate(productReceipt);
	}

	@Override
	protected void onUpdate(ProductReceipt productReceipt) throws Exception {
		fixAssociations(productReceipt);
		super.onUpdate(productReceipt);
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
	protected void onDelete(ProductReceipt productReceipt) throws Exception {
		List<ProductReceiptDetail> details = getDetails(productReceipt.getId());
		for (ProductReceiptDetail detail : details) {
			detail.setUpdater(productReceipt.getUpdater());
			detail.setLastUpdateDate(new Date());
		}
		productReceipt.setDetails(details);
		super.onDelete(productReceipt);
	}

	private List<ProductReceiptDetail> getDetails(Long productReceiptId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductReceiptDetail> cq = cb.createQuery(ProductReceiptDetail.class);
		Root<ProductReceiptDetail> rt = cq.from(ProductReceiptDetail.class);

		Expression<Boolean> exp = cb.equal(rt.join("productReceipt").get("id"), productReceiptId);
		cq.where(exp);

		TypedQuery<ProductReceiptDetail> query = em.createQuery(cq);

		return query.getResultList();
	}

	private void fixAssociations(ProductReceipt productReceipt) {
		List<Integer> usedDetailIds = null;
		List<ProductReceiptDetail> oldDetails = null;

		final List<ProductReceiptDetail> currentDetails = productReceipt.getDetails();

		if (productReceipt.getId() != null) {
			oldDetails = getDetails(productReceipt.getId());
			usedDetailIds = oldDetails.stream().map(x -> x.getId().getDetailId()).collect(Collectors.toList());
		} else {
			usedDetailIds = new ArrayList<>();
		}

		if (currentDetails != null) {
			final List<Product> products = getProducts(
					currentDetails.stream().map(x -> x.getProduct().getId()).collect(Collectors.toList()));

			for (ProductReceiptDetail detail : currentDetails) {
				detail.setProductReceipt(productReceipt);
				detail.setCreator(productReceipt.getCreator());
				detail.setUpdater(productReceipt.getUpdater());
				detail.setCreationDate(productReceipt.getCreationDate());
				detail.setLastUpdateDate(productReceipt.getLastUpdateDate());

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

			for (ProductReceiptDetail detail : oldDetails) {
				if (currentDetails != null) {
					if (!currentDetails.contains(detail)) {
						detail.setUpdater(productReceipt.getUpdater());
						detail.setCreationDate(productReceipt.getCreationDate());
						detail.setLastUpdateDate(productReceipt.getLastUpdateDate());

						em.remove(detail);
					}
				} else {
					em.remove(detail);
				}
			}
		}

		if (productReceipt.getCurrency() != null) {
			productReceipt.setCurrency(EntityManagerUtil.find(this.getEntityManager(), Currency.class, productReceipt.getCurrency()));
		}
		if (productReceipt.getVendor() != null) {
			productReceipt.setVendor(EntityManagerUtil.find(this.getEntityManager(), Vendor.class, productReceipt.getVendor()));
		}
		if (productReceipt.getLocation() != null) {
			productReceipt.setLocation(EntityManagerUtil.find(this.getEntityManager(), Location.class, productReceipt.getLocation()));
		}
	}

	private void setProduct(final ProductReceiptDetail detail, final List<Product> products) {
		final Product product = detail.getProduct();
		final Optional<Product> optional = products.stream().filter(x -> x.equals(product)).findFirst();
		if (optional.isPresent()) {
			detail.setProduct(optional.get());
		} else {
			throw new NonexistentEntityException(
					String.format("No %s with ID '%s' was found.", Product.class.getSimpleName(), product.getId()));
		}
	}

	private void setDetailId(final ProductReceiptDetail detail, final List<Integer> usedDetailIds) {
		final int detailId = NumberHelper.getLeastAvailable(usedDetailIds);
		usedDetailIds.add(detailId);

		final ProductReceiptDetailId id = new ProductReceiptDetailId();
		id.setDetailId(detailId);
		detail.setId(id);
	}

}
