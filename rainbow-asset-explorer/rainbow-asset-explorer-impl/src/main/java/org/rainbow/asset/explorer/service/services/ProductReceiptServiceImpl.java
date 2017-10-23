package org.rainbow.asset.explorer.service.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.ProductReceipt;
import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetail;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductReceiptReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.ProductReceiptDetailsNullOrEmptyException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class ProductReceiptServiceImpl extends ServiceImpl<ProductReceipt> implements ProductReceiptService {

	private InventoryManager inventoryManager;

	public ProductReceiptServiceImpl() {
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(InventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	@Override
	public List<ProductReceiptDetail> getDetails(Object productReceiptId) throws Exception {
		checkDependencies();
		return this.getDao().findById(productReceiptId).getDetails();
	}

	@Override
	protected void validate(ProductReceipt productReceipt, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			List<ProductReceiptDetail> details = productReceipt.getDetails();
			if (details == null || details.isEmpty()
					|| details.stream().filter(x -> x.getQuantity() > 0).count() == 0) {
				throw new ProductReceiptDetailsNullOrEmptyException();
			}
			if (DaoUtil.isDuplicate(this.getDao(), "referenceNumber", productReceipt.getReferenceNumber(),
					productReceipt.getId(), operation)) {
				throw new DuplicateProductReceiptReferenceNumberException(productReceipt.getReferenceNumber());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void create(ProductReceipt productReceipt) throws Exception {
		super.create(productReceipt);
		onCreated(productReceipt);
	}

	@Override
	public void create(List<ProductReceipt> productReceipts) throws Exception {
		super.create(productReceipts);
		for (ProductReceipt productReceipt : productReceipts) {
			onCreated(productReceipt);
		}
	}

	@Override
	public void update(ProductReceipt productReceipt) throws Exception {
		checkDependencies();
		onUpdate(productReceipt);
		super.update(productReceipt);
	}

	@Override
	public void update(List<ProductReceipt> productReceipts) throws Exception {
		checkDependencies();
		for (ProductReceipt productReceipt : productReceipts) {
			onUpdate(productReceipt);
		}
		super.update(productReceipts);
	}

	@Override
	public void delete(ProductReceipt productReceipt) throws Exception {
		checkDependencies();
		onDelete(productReceipt);
		super.delete(productReceipt);
	}

	@Override
	public void delete(List<ProductReceipt> productReceipts) throws Exception {
		checkDependencies();
		for (ProductReceipt productReceipt : productReceipts) {
			onDelete(productReceipt);
		}
		super.delete(productReceipts);
	}

	private void onCreated(ProductReceipt productReceipt) throws Exception {
		final List<ProductReceiptDetail> details = productReceipt.getDetails();
		if (details != null) {
			final Map<Long, Short> productsCount = new HashMap<>();
			for (ProductReceiptDetail detail : details) {
				final Long productId = detail.getProduct().getId();
				if (!productsCount.containsKey(productId)) {
					productsCount.put(productId, detail.getQuantity());
				} else {
					productsCount.replace(productId, (short) (productsCount.get(productId) + detail.getQuantity()));
				}
			}
			final Long locationId = productReceipt.getLocation().getId();
			this.getInventoryManager().add(locationId, productsCount);
		}
	}

	private void onUpdate(ProductReceipt productReceipt) throws Exception {
		final Location newLocation = productReceipt.getLocation();
		final Long newLocationId = newLocation.getId();
		final List<Long> productIds = new ArrayList<>();
		final Map<Long, Short> newProductsCount = new HashMap<>();
		final List<ProductReceiptDetail> newDetails = productReceipt.getDetails();

		if (newDetails != null) {
			for (ProductReceiptDetail detail : newDetails) {
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
		final ProductReceipt persistentProductReceipt = this.getDao().findById(productReceipt.getId());
		final Location oldLocation = persistentProductReceipt.getLocation();
		final Long oldLocationId = oldLocation.getId();
		final Map<Long, Short> oldProductsCount = new HashMap<>();
		final List<ProductReceiptDetail> oldDetails = getDetails(productReceipt.getId());

		if (oldDetails != null) {
			for (ProductReceiptDetail detail : oldDetails) {
				Long productId = detail.getProduct().getId();

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
				this.getInventoryManager().subtract(oldLocationId, removals);
			}
			if (!additions.isEmpty()) {
				this.getInventoryManager().add(oldLocationId, additions);
			}
		} else {
			// First, we have to subtract the products that were added to the
			// inventory of the old location.
			this.getInventoryManager().subtract(oldLocationId, oldProductsCount);
			// Now, we have to add the products to the inventory of the new
			// location.
			this.getInventoryManager().add(newLocationId, newProductsCount);
		}
	}

	private void onDelete(ProductReceipt productReceipt) throws Exception {
		final ProductReceipt persistentProductReceipt = this.getDao().findById(productReceipt.getId());
		final Long locationId = persistentProductReceipt.getLocation().getId();
		final List<ProductReceiptDetail> details = getDetails(productReceipt.getId());
		final Map<Long, Short> removals = new HashMap<>();

		if (details != null) {
			for (ProductReceiptDetail detail : details) {
				Long productId = detail.getProduct().getId();
				if (!removals.containsKey(productId)) {
					removals.put(productId, detail.getQuantity());
				} else {
					removals.replace(productId, (short) (removals.get(productId) + detail.getQuantity()));
				}
			}
			this.getInventoryManager().subtract(locationId, removals);
		}
	}

	@Override
	protected void checkDependencies() {
		super.checkDependencies();
		if (this.getInventoryManager() == null) {
			throw new IllegalStateException("The inventory manager cannot be null.");
		}
	}
}
