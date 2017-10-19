package org.rainbow.asset.explorer.service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.ProductIssue;
import org.rainbow.asset.explorer.orm.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductIssueReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.service.exceptions.ProductIssueDetailsNullOrEmptyException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class ProductIssueServiceImpl extends ServiceImpl<ProductIssue> implements ProductIssueService {

	private InventoryManager inventoryManager;

	public ProductIssueServiceImpl() {
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(InventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	@Override
	public List<ProductIssueDetail> getDetails(Object productIssueId) throws Exception {
		checkDependencies();
		return this.getDao().findById(productIssueId).getDetails();
	}

	@Override
	protected void validate(ProductIssue productIssue, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			List<ProductIssueDetail> details = productIssue.getDetails();
			if (details == null || details.isEmpty()
					|| details.stream().filter(x -> x.getQuantity() > 0).count() == 0) {
				throw new ProductIssueDetailsNullOrEmptyException();
			}
			if (DaoUtil.isDuplicate(this.getDao(), "referenceNumber", productIssue.getReferenceNumber(),
					productIssue.getId(), operation)) {
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
		super.create(productIssue);
		onCreated(productIssue);
	}

	@Override
	public void create(List<ProductIssue> productIssues) throws Exception {
		super.create(productIssues);
		for (ProductIssue productIssue : productIssues) {
			onCreated(productIssue);
		}
	}

	@Override
	public void update(ProductIssue productIssue) throws Exception {
		checkDependencies();
		onUpdate(productIssue);
		super.update(productIssue);
	}

	@Override
	public void update(List<ProductIssue> productIssues) throws Exception {
		checkDependencies();
		for (ProductIssue productIssue : productIssues) {
			onUpdate(productIssue);
		}
		super.update(productIssues);
	}

	@Override
	public void delete(ProductIssue productIssue) throws Exception {
		checkDependencies();
		onDelete(productIssue);
		super.delete(productIssue);
	}

	@Override
	public void delete(List<ProductIssue> productIssues) throws Exception {
		checkDependencies();
		for (ProductIssue productIssue : productIssues) {
			onDelete(productIssue);
		}
		super.delete(productIssues);
	}

	private void onCreated(ProductIssue productIssue) throws InsufficientInventoryException, Exception {
		final List<ProductIssueDetail> details = productIssue.getDetails();
		if (details != null) {
			final Map<Long, Short> productsCount = new HashMap<>();
			for (ProductIssueDetail detail : details) {
				Long productId = detail.getProduct().getId();

				if (!productsCount.containsKey(productId)) {
					productsCount.put(productId, detail.getQuantity());
				} else {
					productsCount.replace(productId, (short) (productsCount.get(productId) + detail.getQuantity()));
				}
			}
			final Long locationId = productIssue.getLocation().getId();
			this.getInventoryManager().subtract(locationId, productsCount);
		}
	}

	private void onUpdate(ProductIssue productIssue) throws Exception {
		final Location newLocation = productIssue.getLocation();
		final Long newLocationId = newLocation.getId();
		final List<Long> productIds = new ArrayList<>();
		final Map<Long, Short> newProductsCount = new HashMap<>();
		final List<ProductIssueDetail> newDetails = productIssue.getDetails();

		if (newDetails != null) {
			for (ProductIssueDetail detail : newDetails) {
				if (detail.getQuantity() > 0) {

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
		}

		final ProductIssue persistentProductIssue = this.getDao().findById(productIssue.getId());
		final Location oldLocation = persistentProductIssue.getLocation();
		final Long oldLocationId = oldLocation.getId();
		final Map<Long, Short> oldProductsCount = new HashMap<>();
		final List<ProductIssueDetail> oldDetails = getDetails(productIssue.getId());

		if (oldDetails != null) {
			for (ProductIssueDetail detail : oldDetails) {
				final Long productId = detail.getProduct().getId();

				productIds.add(productId);

				if (!oldProductsCount.containsKey(productId)) {
					oldProductsCount.put(productId, detail.getQuantity());
				} else {
					oldProductsCount.replace(productId,
							(short) (oldProductsCount.get(productId) + detail.getQuantity()));
				}
			}
		}

		final boolean sameLocation = oldLocation.equals(newLocation);

		if (sameLocation) {
			final Map<Long, Short> additions = new HashMap<>();
			final Map<Long, Short> removals = new HashMap<>();

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
				this.getInventoryManager().subtract(oldLocationId, removals);
			}
			if (!additions.isEmpty()) {
				this.getInventoryManager().add(oldLocationId, additions);
			}
		} else {
			// First, we have to restitute the products that were removed from
			// the inventory of the old location.
			this.getInventoryManager().add(oldLocationId, oldProductsCount);
			// Now, we have to subtract the products from the inventory of the
			// new location.
			this.getInventoryManager().subtract(newLocationId, newProductsCount);
		}
	}

	private void onDelete(ProductIssue productIssue) throws Exception {
		final ProductIssue persistentProductIssue = this.getDao().findById(productIssue.getId());
		final Long locationId = persistentProductIssue.getLocation().getId();
		final List<ProductIssueDetail> details = persistentProductIssue.getDetails();
		final Map<Long, Short> additions = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			final Long productId = detail.getProduct().getId();

			if (!additions.containsKey(productId)) {
				additions.put(productId, detail.getQuantity());
			} else {
				additions.replace(productId, (short) (additions.get(productId) + detail.getQuantity()));
			}
		}

		this.getInventoryManager().add(locationId, additions);
	}

	@Override
	protected void checkDependencies() {
		super.checkDependencies();
		if (this.getInventoryManager() == null) {
			throw new IllegalStateException("The inventory manager cannot be null.");
		}
	}
}
