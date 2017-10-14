package org.rainbow.asset.explorer.service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.quartz.SchedulerException;
import org.rainbow.asset.explorer.orm.entities.AlertType;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.orm.entities.ProductInventoryId;
import org.rainbow.asset.explorer.persistence.dao.LocationDao;
import org.rainbow.asset.explorer.persistence.dao.ProductDao;
import org.rainbow.asset.explorer.persistence.dao.ProductInventoryDao;
import org.rainbow.asset.explorer.scheduling.AlertScheduler;
import org.rainbow.asset.explorer.service.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;
import org.rainbow.criteria.PathImpl;
import org.rainbow.criteria.PredicateBuilderImpl;
import org.rainbow.criteria.SearchOptionsImpl;
import org.rainbow.persistence.exceptions.NonexistentEntityException;

/**
 *
 * @author Biya-Bi
 */
public class InventoryManagerImpl implements InventoryManager {
	private AlertScheduler alertScheduler;
	private ProductInventoryDao productInventoryDao;
	private LocationDao locationDao;
	private ProductDao productDao;

	public AlertScheduler getAlertScheduler() {
		return alertScheduler;
	}

	public void setAlertScheduler(AlertScheduler alertScheduler) {
		this.alertScheduler = alertScheduler;
	}

	public ProductInventoryDao getProductInventoryDao() {
		return productInventoryDao;
	}

	public void setProductInventoryDao(ProductInventoryDao productInventoryDao) {
		this.productInventoryDao = productInventoryDao;
	}

	public LocationDao getLocationDao() {
		return locationDao;
	}

	public void setLocationDao(LocationDao locationDao) {
		this.locationDao = locationDao;
	}

	public ProductDao getProductDao() {
		return productDao;
	}

	public void setProductDao(ProductDao productDao) {
		this.productDao = productDao;
	}

	private List<ProductInventory> getInventory(Long locationId, List<Long> productIds) throws Exception {
		validateLocation(locationId);
		validateProducts(productIds);

		PredicateBuilderImpl builder = new PredicateBuilderImpl();

		return this.getProductInventoryDao()
				.find(new SearchOptionsImpl(builder.and(builder.equal(new PathImpl("id.locationId"), locationId),
						builder.in(new PathImpl("id.productId"), productIds))));
	}

	@Override
	public void add(Long locationId, Map<Long, Short> productIdsQuantities) throws Exception {
		checkDependencies();

		Objects.requireNonNull(locationId);
		Objects.requireNonNull(productIdsQuantities);

		validateLocation(locationId);
		List<Long> productIds = new ArrayList<Long>(productIdsQuantities.keySet());
		validateProducts(productIds);

		List<ProductInventory> added_inventories = new ArrayList<>();
		List<ProductInventory> updated_inventories = new ArrayList<>();

		List<ProductInventory> productInventories = getInventory(locationId, productIds);

		for (Long productId : productIds) {
			boolean found = false;
			Short addedQuantity = productIdsQuantities.get(productId);
			for (ProductInventory productInventory : productInventories) {
				if (productInventory.getId().getProductId().equals(productId)) {
					productInventory.setQuantity((short) (productInventory.getQuantity() + addedQuantity));
					if (!updated_inventories.contains(productInventory)) {
						updated_inventories.add(productInventory);
					}
					found = true;
					break;
				}
			}
			if (!found) {
				ProductInventory productInventory = new ProductInventory();
				productInventory.setId(new ProductInventoryId(productId, locationId));
				productInventory.setQuantity(addedQuantity);
				productInventories.add(productInventory);
				added_inventories.add(productInventory);
			}
		}
		if (!added_inventories.isEmpty()) {
			this.getProductInventoryDao().create(added_inventories);
		}
		if (!updated_inventories.isEmpty()) {
			this.getProductInventoryDao().update(updated_inventories);
		}
		try {
			this.getAlertScheduler().schedule(AlertType.RECOVERY, locationId, productIds);
		} catch (SchedulerException e) {
			throw new RainbowAssetExplorerException(e);
		}
	}

	@Override
	public void substract(Long locationId, Map<Long, Short> productIdsQuantities) throws Exception {
		checkDependencies();

		Objects.requireNonNull(locationId);
		Objects.requireNonNull(productIdsQuantities);

		validateLocation(locationId);
		List<Long> productIds = new ArrayList<Long>(productIdsQuantities.keySet());
		validateProducts(productIds);

		List<ProductInventory> updated_inventories = new ArrayList<>();
		List<ProductInventory> deleted_inventories = new ArrayList<>();

		List<ProductInventory> productInventories = getInventory(locationId, productIds);
		for (Long productId : productIds) {
			boolean found = false;
			Short substractedQuantity = productIdsQuantities.get(productId);
			for (ProductInventory productInventory : productInventories) {
				if (productInventory.getId().getProductId().equals(productId)) {
					short availableQuantity = productInventory.getQuantity();
					short remainingQuantity = (short) (availableQuantity - substractedQuantity);
					if (remainingQuantity < 0) {
						throw new InsufficientInventoryException(locationId, productId, availableQuantity,
								substractedQuantity);
					}
					productInventory.setQuantity(remainingQuantity);
					if (remainingQuantity > 0) {
						updated_inventories.add(productInventory);
					} else {
						deleted_inventories.add(productInventory);
					}
					found = true;
					break;
				}
			}
			if (!found) {
				throw new InsufficientInventoryException(locationId, productId, (short) 0, substractedQuantity);
			}
		}
		if (!updated_inventories.isEmpty()) {
			this.getProductInventoryDao().update(updated_inventories);
		}
		if (!deleted_inventories.isEmpty()) {
			this.getProductInventoryDao().delete(deleted_inventories);
		}
		try {
			this.getAlertScheduler().schedule(AlertType.WARNING, locationId, productIds);
		} catch (SchedulerException e) {
			throw new RainbowAssetExplorerException(e);
		}
	}

	private void validateLocation(Long locationId) throws Exception {
		PredicateBuilderImpl builder = new PredicateBuilderImpl();

		long count = this.getLocationDao().count(builder.equal(new PathImpl("id"), locationId));

		if (count == 0) {
			throw new NonexistentEntityException(
					String.format("No %s with ID '%s' was found.", Location.class.getSimpleName(), locationId));
		}
	}

	private List<Long> getProductIds(List<Long> productIds) throws Exception {
		PredicateBuilderImpl builder = new PredicateBuilderImpl();

		List<Product> products = this.getProductDao()
				.find(new SearchOptionsImpl(builder.in(new PathImpl("id"), productIds)));

		if (products == null) {
			return new ArrayList<Long>();
		}

		return products.stream().map(x -> x.getId()).collect(Collectors.toList());
	}

	private void validateProducts(List<Long> productIds) throws Exception {
		List<Long> existingProductIds = getProductIds(productIds);
		Long id = productIds.stream().filter(x -> !existingProductIds.contains(x)).findFirst().orElse(null);
		if (id != null) {
			throw new NonexistentEntityException(
					String.format("No %s with ID '%s' was found.", Product.class.getSimpleName(), id));
		}
	}

	private void checkDependencies() {
		if (this.getAlertScheduler() == null) {
			throw new IllegalStateException("The alert scheduler cannot be null.");
		}
		if (this.getProductInventoryDao() == null) {
			throw new IllegalStateException("The product inventory dao cannot be null.");
		}
		if (this.getLocationDao() == null) {
			throw new IllegalStateException("The location dao cannot be null.");
		}
		if (this.getProductDao() == null) {
			throw new IllegalStateException("The product dao cannot be null.");
		}
	}

}
