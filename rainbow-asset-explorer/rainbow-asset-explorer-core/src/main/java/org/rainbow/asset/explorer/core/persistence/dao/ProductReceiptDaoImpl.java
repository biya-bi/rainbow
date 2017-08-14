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

import org.rainbow.asset.explorer.core.entities.Currency;
import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.entities.ProductReceipt;
import org.rainbow.asset.explorer.core.entities.ProductReceiptDetail;
import org.rainbow.asset.explorer.core.entities.ProductReceiptDetailId;
import org.rainbow.asset.explorer.core.entities.Vendor;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateProductReceiptReferenceNumberException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ProductReceiptDetailsNullOrEmptyException;
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
public class ProductReceiptDaoImpl extends TrackableDaoImpl<ProductReceipt, Long> implements ProductReceiptDao {

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

	public ProductReceiptDaoImpl() {
		super(ProductReceipt.class);
	}

	@Override
	protected void validate(ProductReceipt productReceipt, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;

		switch (operation) {
		case CREATE: {
			id = null;
		}
		case UPDATE: {
			id = productReceipt.getId();
			if (helper.isDuplicate(ProductReceipt.class, productReceipt.getReferenceNumber(), "referenceNumber", "id",
					id)) {
				throw new DuplicateProductReceiptReferenceNumberException(productReceipt.getReferenceNumber());
			}
			break;
		}
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void create(ProductReceipt productReceipt) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);

		Location location = helper.find(Location.class, productReceipt.getLocation());

		productReceipt.setLocation(location);
		Long locationId = location.getId();

		Vendor vendor = productReceipt.getVendor();
		if (vendor != null) {
			productReceipt.setVendor(helper.find(Vendor.class, vendor));
		}

		Currency currency = productReceipt.getCurrency();
		if (currency != null) {
			productReceipt.setCurrency(helper.find(Currency.class, currency));
		}

		if (productReceipt.getDetails() == null || productReceipt.getDetails().isEmpty()) {
			throw new ProductReceiptDetailsNullOrEmptyException();
		}

		List<Long> productIds = new ArrayList<>();

		List<ProductReceiptDetail> details = productReceipt.getDetails();

		int detailId = 0;
		int noOfDetailsWithPositiveQuantities = 0;

		Map<Long, Short> productsCount = new HashMap<>();

		// We validate the Product Receipt
		validate(productReceipt, UpdateOperation.CREATE);
		// We persist and flush here because we need the Id of the product
		// receipt so that we can set it in product receipt details.
		em.persist(productReceipt);
		em.flush();

		for (ProductReceiptDetail detail : details) {
			detail.setCreator(productReceipt.getCreator());
			detail.setUpdater(productReceipt.getUpdater());

			Long productId = detail.getProduct().getId();

			if (detail.getQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;

				ProductReceiptDetailId productReceiptDetailId = detail.getId();
				if (productReceiptDetailId == null) {
					productReceiptDetailId = new ProductReceiptDetailId(productReceipt.getId(), ++detailId);
					detail.setId(productReceiptDetailId);
				} else {
					productReceiptDetailId.setDetailId(++detailId);
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
			throw new ProductReceiptDetailsNullOrEmptyException();
		}

		List<Product> persistentProducts = getPersistentProducts(productIds);

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!persistentProducts.contains(detail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", productId));
			}
			detail.setProduct(persistentProducts.get(persistentProducts.indexOf(detail.getProduct())));

			em.persist(detail);
		}

		inventoryManager.add(locationId, productsCount);
	}

	private int getDetailId(List<ProductReceiptDetail> oldDetails, List<ProductReceiptDetail> newDetails) {
		List<Integer> detailIds = new ArrayList<>();
		for (ProductReceiptDetail oldDetail : oldDetails) {
			Integer detailId = oldDetail.getId().getDetailId();
			if (!detailIds.contains(detailId)) {
				detailIds.add(detailId);
			}
		}
		for (ProductReceiptDetail newDetail : newDetails) {
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
	public void update(ProductReceipt productReceipt) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);

		Location newLocation = helper.find(Location.class, productReceipt.getLocation());

		productReceipt.setLocation(newLocation);
		Long newLocationId = newLocation.getId();

		Vendor vendor = productReceipt.getVendor();
		if (vendor != null) {
			productReceipt.setVendor(helper.find(Vendor.class, vendor));
		}

		Currency currency = productReceipt.getCurrency();
		if (currency != null) {
			productReceipt.setCurrency(helper.find(Currency.class, currency));
		}

		if (productReceipt.getDetails() == null || productReceipt.getDetails().isEmpty()) {
			throw new ProductReceiptDetailsNullOrEmptyException();
		}

		List<Long> productIds = new ArrayList<>();

		List<ProductReceiptDetail> newDetails = productReceipt.getDetails();

		Map<Long, Short> newProductsCount = new HashMap<>();

		int noOfDetailsWithPositiveQuantities = 0;
		for (ProductReceiptDetail detail : newDetails) {
			detail.setUpdater(productReceipt.getUpdater());

			if (detail.getQuantity() > 0) {
				noOfDetailsWithPositiveQuantities++;

				ProductReceiptDetailId productReceiptDetailId = detail.getId();
				if (productReceiptDetailId == null) {
					productReceiptDetailId = new ProductReceiptDetailId(productReceipt.getId(), 0);
					detail.setId(productReceiptDetailId);
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
			throw new ProductReceiptDetailsNullOrEmptyException();
		}

		ProductReceipt persistentProductReceipt = getPersistent(productReceipt);
		Location oldLocation = persistentProductReceipt.getLocation();

		Long oldLocationId = oldLocation.getId();

		Map<Long, Short> oldProductsCount = new HashMap<>();

		List<ProductReceiptDetail> oldDetails = getDetails(productReceipt.getId());

		for (ProductReceiptDetail detail : oldDetails) {
			Long productId = detail.getProduct().getId();

			productIds.add(productId);

			if (!oldProductsCount.containsKey(productId)) {
				oldProductsCount.put(productId, detail.getQuantity());
			} else {
				oldProductsCount.replace(productId, (short) (oldProductsCount.get(productId) + detail.getQuantity()));
			}
		}

		List<Product> persistentProducts = getPersistentProducts(productIds);

		for (ProductReceiptDetail newDetail : newDetails) {
			Long productId = newDetail.getProduct().getId();
			if (!persistentProducts.contains(newDetail.getProduct())) {
				throw new NonexistentEntityException(
						String.format("The product with ID '%s' does not exist.", productId));
			}
			newDetail.setProduct(persistentProducts.get(persistentProducts.indexOf(newDetail.getProduct())));

			if (!oldDetails.contains(newDetail)) {
				ProductReceiptDetailId productReceiptDetailId = newDetail.getId();
				if (productReceiptDetailId == null) {
					productReceiptDetailId = new ProductReceiptDetailId(productReceipt.getId(),
							getDetailId(oldDetails, newDetails));
					newDetail.setId(productReceiptDetailId);
				} else {
					productReceiptDetailId.setDetailId(getDetailId(oldDetails, newDetails));
				}

				em.persist(newDetail);
			} else {
				em.merge(newDetail);
			}
		}
		for (ProductReceiptDetail oldDetail : oldDetails) {
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
						additions.put(productId, (short) (newQuantity - oldQuantity));
					} else if (newQuantity < oldQuantity) {
						removals.put(productId, (short) (oldQuantity - newQuantity));
					}
				} else {
					additions.put(productId, newProductsCount.get(productId));
				}
			}
			for (Long productId : oldProductsCount.keySet()) {
				if (!newProductsCount.containsKey(productId)) {
					removals.put(productId, oldProductsCount.get(productId));
				}
			}

			if (!removals.isEmpty()) {
				inventoryManager.substract(oldLocationId, removals);
			}
			if (!additions.isEmpty()) {
				inventoryManager.add(oldLocationId, additions);
			}
		} else {
			// First, we have to subtract the products that were added to the
			// inventory of the old location.
			inventoryManager.substract(oldLocationId, oldProductsCount);
			// Now, we have to add the products to the inventory of the new
			// location.
			inventoryManager.add(newLocationId, newProductsCount);
		}

		super.update(productReceipt);
	}

	@Override
	public void delete(ProductReceipt productReceipt) throws Exception {
		ProductReceipt persistentProductReceipt = getPersistent(productReceipt);
		Long locationId = persistentProductReceipt.getLocation().getId();
		List<ProductReceiptDetail> details = getDetails(productReceipt.getId());

		Map<Long, Short> removals = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			detail.setUpdater(productReceipt.getUpdater());
			detail.setLastUpdateDate(new Date());

			Long productId = detail.getProduct().getId();
			if (!removals.containsKey(productId)) {
				removals.put(productId, detail.getQuantity());
			} else {
				removals.replace(productId, (short) (removals.get(productId) + detail.getQuantity()));
			}
			em.remove(detail);
		}

		inventoryManager.substract(locationId, removals);

		super.delete(productReceipt);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.asset.explorer.core.persistence.dao.ProductReceiptDao#
	 * getDetails(java.lang.Long)
	 */
	@Override
	public List<ProductReceiptDetail> getDetails(Long productReceiptId) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductReceiptDetail> cq = cb.createQuery(ProductReceiptDetail.class);
		Root<ProductReceiptDetail> rt = cq.from(ProductReceiptDetail.class);

		Expression<Boolean> exp = cb.equal(rt.get("id").get("productReceiptId"), productReceiptId);
		cq.where(exp);

		TypedQuery<ProductReceiptDetail> query = em.createQuery(cq);

		return query.getResultList();
	}
}
