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
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

import org.rainbow.asset.explorer.core.entities.Department;
import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.entities.ProductIssue;
import org.rainbow.asset.explorer.core.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.core.entities.ProductIssueDetailId;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateProductIssueReferenceNumberException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ProductIssueDetailsNullOrEmptyException;
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
public class ProductIssueDaoImpl extends TrackableDaoImpl<ProductIssue, Long> implements ProductIssueDao {

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

	public ProductIssueDaoImpl() {
		super(ProductIssue.class);
	}

	@Override
	protected void validate(ProductIssue productIssue, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;

		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = productIssue.getId();
			if (helper.isDuplicate(ProductIssue.class, productIssue.getReferenceNumber(), "referenceNumber", "id",
					id)) {
				throw new DuplicateProductIssueReferenceNumberException(productIssue.getReferenceNumber());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void create(ProductIssue productIssue) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);

		Location location = helper.find(Location.class, productIssue.getLocation());

		productIssue.setLocation(location);
		Long locationId = location.getId();

		Department department = productIssue.getDepartment();
		if (department != null) {
			productIssue.setDepartment(helper.find(Department.class, department));
		}

		if (productIssue.getDetails() == null || productIssue.getDetails().isEmpty()) {
			throw new ProductIssueDetailsNullOrEmptyException();
		}

		List<Long> productIds = new ArrayList<>();

		List<ProductIssueDetail> details = productIssue.getDetails();

		int detailId = 0;
		int noOfDetailsWithPositiveQuantities = 0;

		Map<Long, Short> productsCount = new HashMap<>();

		// We validate the Product Issue
		validate(productIssue, UpdateOperation.CREATE);
		// We persist and flush here because we need the Id of the product issue
		// so that we can set it in product issue details.
		em.persist(productIssue);
		em.flush();

		for (ProductIssueDetail detail : details) {
			detail.setCreator(productIssue.getCreator());
			detail.setUpdater(productIssue.getUpdater());

			Long productId = detail.getProduct().getId();

			if (detail.getQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;

				ProductIssueDetailId productIssueDetailId = detail.getId();
				if (productIssueDetailId == null) {
					productIssueDetailId = new ProductIssueDetailId(productIssue.getId(), ++detailId);
					detail.setId(productIssueDetailId);
				} else {
					productIssueDetailId.setDetailId(++detailId);
				}
				productIds.add(productId);
			}
			if (!productsCount.containsKey(productId)) {
				productsCount.put(productId, detail.getQuantity());
			} else {
				productsCount.replace(productId, (short) (productsCount.get(productId) + detail.getQuantity()));
			}
		}

		if (noOfDetailsWithPositiveQuantities == 0) {
			throw new ProductIssueDetailsNullOrEmptyException();
		}

		List<Product> persistentProducts = getPersistentProducts(productIds);

		for (ProductIssueDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!persistentProducts.contains(detail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", productId));
			}
			detail.setProduct(persistentProducts.get(persistentProducts.indexOf(detail.getProduct())));

			em.persist(detail);
		}

		inventoryManager.substract(locationId, productsCount);
	}

	private int getDetailId(List<ProductIssueDetail> oldDetails, List<ProductIssueDetail> newDetails) {
		List<Integer> detailIds = new ArrayList<>();
		for (ProductIssueDetail oldDetail : oldDetails) {
			Integer detailId = oldDetail.getId().getDetailId();
			if (!detailIds.contains(detailId)) {
				detailIds.add(detailId);
			}
		}
		for (ProductIssueDetail newDetail : newDetails) {
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
	public void update(ProductIssue productIssue) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);

		Location newLocation = helper.find(Location.class, productIssue.getLocation());

		productIssue.setLocation(newLocation);
		Long newLocationId = newLocation.getId();

		Department department = productIssue.getDepartment();
		if (department != null) {
			productIssue.setDepartment(helper.find(Department.class, department));
		}

		if (productIssue.getDetails() == null || productIssue.getDetails().isEmpty()) {
			throw new ProductIssueDetailsNullOrEmptyException();
		}

		List<Long> productIds = new ArrayList<>();

		List<ProductIssueDetail> newDetails = productIssue.getDetails();

		Map<Long, Short> newProductsCount = new HashMap<>();

		int noOfDetailsWithPositiveQuantities = 0;
		for (ProductIssueDetail detail : newDetails) {
			detail.setUpdater(productIssue.getUpdater());

			if (detail.getQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;

				ProductIssueDetailId productIssueDetailId = detail.getId();
				if (productIssueDetailId == null) {
					productIssueDetailId = new ProductIssueDetailId(productIssue.getId(), 0);
					detail.setId(productIssueDetailId);
				}

				Long productId = detail.getProduct().getId();

				productIds.add(productId);

				if (!newProductsCount.containsKey(productId)) {
					newProductsCount.put(productId, detail.getQuantity());
				} else {
					newProductsCount.replace(productId,
							(short) (newProductsCount.get(productId) + detail.getQuantity()));
				}
			}
		}

		if (noOfDetailsWithPositiveQuantities == 0) {
			throw new ProductIssueDetailsNullOrEmptyException();
		}

		ProductIssue persistentProductIssue = getPersistent(productIssue);
		Location oldLocation = persistentProductIssue.getLocation();

		Long oldLocationId = oldLocation.getId();

		Map<Long, Short> oldProductsCount = new HashMap<>();

		List<ProductIssueDetail> oldDetails = getDetails(productIssue.getId());

		for (ProductIssueDetail detail : oldDetails) {
			detail.setUpdater(productIssue.getUpdater());

			Long productId = detail.getProduct().getId();

			productIds.add(productId);

			if (!oldProductsCount.containsKey(productId)) {
				oldProductsCount.put(productId, detail.getQuantity());
			} else {
				oldProductsCount.replace(productId, (short) (oldProductsCount.get(productId) + detail.getQuantity()));
			}
		}
		List<Product> persistentProducts = getPersistentProducts(productIds);

		for (ProductIssueDetail newDetail : newDetails) {
			Long productId = newDetail.getProduct().getId();
			if (!persistentProducts.contains(newDetail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", productId));
			}
			newDetail.setProduct(persistentProducts.get(persistentProducts.indexOf(newDetail.getProduct())));

			if (!oldDetails.contains(newDetail)) {

				ProductIssueDetailId productIssueDetailId = newDetail.getId();
				if (productIssueDetailId == null) {
					productIssueDetailId = new ProductIssueDetailId(productIssue.getId(),
							getDetailId(oldDetails, newDetails));
					newDetail.setId(productIssueDetailId);
				} else {
					productIssueDetailId.setDetailId(getDetailId(oldDetails, newDetails));
				}

				em.persist(newDetail);
			} else {
				em.merge(newDetail);
			}
		}
		for (ProductIssueDetail oldDetail : oldDetails) {
			if (!newDetails.contains(oldDetail)) {
				em.remove(oldDetail);
			}
		}

		boolean sameLocation = oldLocation.equals(newLocation);

		if (sameLocation) {
			Map<Long, Short> additions = new HashMap<>();
			Map<Long, Short> removals = new HashMap<>();

			for (Long productId : newProductsCount.keySet()) {
				if (oldProductsCount.containsKey(productId)) {
					short oldQuantity = oldProductsCount.get(productId);
					short newQuantity = newProductsCount.get(productId);
					if (newQuantity > oldQuantity) {
						removals.put(productId, (short) (newQuantity - oldQuantity));
					} else if (newQuantity < oldQuantity) {
						additions.put(productId, (short) (oldQuantity - newQuantity));
					}
				} else {
					removals.put(productId, newProductsCount.get(productId));
				}
			}
			for (Long productId : oldProductsCount.keySet()) {
				if (!newProductsCount.containsKey(productId)) {
					additions.put(productId, oldProductsCount.get(productId));
				}
			}

			if (!removals.isEmpty()) {
				inventoryManager.substract(oldLocationId, removals);
			}
			if (!additions.isEmpty()) {
				inventoryManager.add(oldLocationId, additions);
			}
		} else {
			// First, we have to restitute the products that were removed from
			// the inventory of the old location.
			inventoryManager.add(oldLocationId, oldProductsCount);
			// Now, we have to substract the products from the inventory of the
			// new location.
			inventoryManager.substract(newLocationId, newProductsCount);
		}
		super.update(productIssue);
	}

	@Override
	public void delete(ProductIssue productIssue) throws Exception {
		ProductIssue persistentProductIssue = getPersistent(productIssue);
		Long locationId = persistentProductIssue.getLocation().getId();
		List<ProductIssueDetail> details = getDetails(productIssue.getId());

		Map<Long, Short> additions = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			detail.setUpdater(productIssue.getUpdater());
			detail.setLastUpdateDate(new Date());

			Long productId = detail.getProduct().getId();
			if (!additions.containsKey(productId)) {
				additions.put(productId, detail.getQuantity());
			} else {
				additions.replace(productId, (short) (additions.get(productId) + detail.getQuantity()));
			}
			em.remove(detail);
		}

		inventoryManager.add(locationId, additions);

		super.delete(productIssue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.asset.explorer.core.persistence.dao.ProductIssueDao#
	 * getDetails(java.lang.Long)
	 */
	@Override
	public List<ProductIssueDetail> getDetails(Long productIssueId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductIssueDetail> cq = cb.createQuery(ProductIssueDetail.class);
		Root<ProductIssueDetail> rt = cq.from(ProductIssueDetail.class);

		Expression<Boolean> exp = cb.equal(rt.get("id").get("productIssueId"), productIssueId);
		cq.where(exp);

		TypedQuery<ProductIssueDetail> query = em.createQuery(cq);

		return query.getResultList();
	}
}
