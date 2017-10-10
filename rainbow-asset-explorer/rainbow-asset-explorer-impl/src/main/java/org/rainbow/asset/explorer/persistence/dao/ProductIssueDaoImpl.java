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

import org.rainbow.asset.explorer.orm.entities.Department;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductIssue;
import org.rainbow.asset.explorer.orm.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.orm.entities.ProductIssueDetailId;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.util.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ProductIssueDaoImpl extends TrackableDaoImpl<ProductIssue> implements ProductIssueDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProductIssueDaoImpl() {
		super(ProductIssue.class);
	}

	@Override
	protected void onCreate(ProductIssue productIssue) throws Exception {
		fixAssociations(productIssue);
		super.onCreate(productIssue);
	}

	@Override
	protected void onUpdate(ProductIssue productIssue) throws Exception {
		fixAssociations(productIssue);
		super.onUpdate(productIssue);
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
	protected void onDelete(ProductIssue productIssue) throws Exception {
		List<ProductIssueDetail> details = getDetails(productIssue.getId());
		for (ProductIssueDetail detail : details) {
			detail.setUpdater(productIssue.getUpdater());
			detail.setLastUpdateDate(new Date());
		}
		productIssue.setDetails(details);
		super.onDelete(productIssue);
	}

	private List<ProductIssueDetail> getDetails(Long productIssueId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductIssueDetail> cq = cb.createQuery(ProductIssueDetail.class);
		Root<ProductIssueDetail> rt = cq.from(ProductIssueDetail.class);

		Expression<Boolean> exp = cb.equal(rt.join("productIssue").get("id"), productIssueId);
		cq.where(exp);

		TypedQuery<ProductIssueDetail> query = em.createQuery(cq);

		return query.getResultList();
	}

	private void fixAssociations(ProductIssue productIssue) {
		List<Integer> usedDetailIds = null;
		List<ProductIssueDetail> oldDetails = null;

		final List<ProductIssueDetail> currentDetails = productIssue.getDetails();

		if (productIssue.getId() != null) {
			oldDetails = getDetails(productIssue.getId());
			usedDetailIds = oldDetails.stream().map(x -> x.getId().getDetailId()).collect(Collectors.toList());
		} else {
			usedDetailIds = new ArrayList<>();
		}

		if (currentDetails != null) {
			final List<Product> products = getProducts(
					currentDetails.stream().map(x -> x.getProduct().getId()).collect(Collectors.toList()));

			for (ProductIssueDetail detail : currentDetails) {
				detail.setProductIssue(productIssue);
				detail.setCreator(productIssue.getCreator());
				detail.setUpdater(productIssue.getUpdater());
				detail.setCreationDate(productIssue.getCreationDate());
				detail.setLastUpdateDate(productIssue.getLastUpdateDate());

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

			for (ProductIssueDetail detail : oldDetails) {
				if (currentDetails != null) {
					if (!currentDetails.contains(detail)) {
						detail.setUpdater(productIssue.getUpdater());
						detail.setCreationDate(productIssue.getCreationDate());
						detail.setLastUpdateDate(productIssue.getLastUpdateDate());

						em.remove(detail);
					}
				} else {
					em.remove(detail);
				}
			}
		}

		if (productIssue.getDepartment() != null) {
			productIssue.setDepartment(EntityManagerUtil.find(this.getEntityManager(), Department.class, productIssue.getDepartment()));
		}
		if (productIssue.getLocation() != null) {
			productIssue.setLocation(EntityManagerUtil.find(this.getEntityManager(), Location.class, productIssue.getLocation()));
		}
	}

	private void setProduct(final ProductIssueDetail detail, final List<Product> products) {
		final Product product = detail.getProduct();
		final Optional<Product> optional = products.stream().filter(x -> x.equals(product)).findFirst();
		if (optional.isPresent()) {
			detail.setProduct(optional.get());
		} else {
			throw new NonexistentEntityException(
					String.format("No %s with ID '%s' was found.", Product.class.getSimpleName(), product.getId()));
		}
	}

	private void setDetailId(final ProductIssueDetail detail, final List<Integer> usedDetailIds) {
		final int detailId = NumberHelper.getLeastAvailable(usedDetailIds);
		usedDetailIds.add(detailId);

		final ProductIssueDetailId id = new ProductIssueDetailId();
		id.setDetailId(detailId);
		detail.setId(id);
	}
}
