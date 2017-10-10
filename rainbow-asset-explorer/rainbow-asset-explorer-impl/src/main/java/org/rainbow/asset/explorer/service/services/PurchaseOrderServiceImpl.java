package org.rainbow.asset.explorer.service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrder;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetail;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderStatus;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.persistence.dao.InventoryManager;
import org.rainbow.asset.explorer.persistence.dao.LocationDao;
import org.rainbow.asset.explorer.persistence.dao.VendorDao;
import org.rainbow.asset.explorer.service.exceptions.DuplicatePurchaseOrderReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderCompleteQuantityOutOfRangeException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderReadOnlyException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderStatusTransitionException;
import org.rainbow.asset.explorer.service.exceptions.VendorInactiveException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class PurchaseOrderServiceImpl extends ServiceImpl<PurchaseOrder> implements PurchaseOrderService {
	private VendorDao vendorDao;
	private LocationDao locationDao;
	private InventoryManager inventoryManager;

	public PurchaseOrderServiceImpl() {
	}

	public VendorDao getVendorDao() {
		return vendorDao;
	}

	public void setVendorDao(VendorDao vendorDao) {
		this.vendorDao = vendorDao;
	}

	public LocationDao getLocationDao() {
		return locationDao;
	}

	public void setLocationDao(LocationDao locationDao) {
		this.locationDao = locationDao;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(InventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	@Override
	public void approve(PurchaseOrder purchaseOrder) throws Exception {
		checkDependencies();
		final PurchaseOrder persistentPurchaseOrder = this.getDao().findById(purchaseOrder.getId());
		persistentPurchaseOrder.setUpdater(purchaseOrder.getUpdater());
		changeStatus(persistentPurchaseOrder, PurchaseOrderStatus.APPROVED);
	}

	@Override
	public void reject(PurchaseOrder purchaseOrder) throws Exception {
		checkDependencies();
		final PurchaseOrder persistentPurchaseOrder = this.getDao().findById(purchaseOrder.getId());
		persistentPurchaseOrder.setUpdater(purchaseOrder.getUpdater());
		changeStatus(persistentPurchaseOrder, PurchaseOrderStatus.REJECTED);
	}

	@Override
	public void complete(PurchaseOrder purchaseOrder, Location location, Map<Long, Short> productsCount)
			throws Exception {
		checkDependencies();
		PurchaseOrder persistentPurchaseOrder = this.getDao().findById(purchaseOrder.getId());

		persistentPurchaseOrder.setUpdater(purchaseOrder.getUpdater());

		Location persistentLocation = this.getLocationDao().findById(location.getId());

		List<PurchaseOrderDetail> details = getDetails(persistentPurchaseOrder.getId());
		persistentPurchaseOrder.setDetails(details);
		validateComplete(persistentPurchaseOrder, productsCount);
		persistentPurchaseOrder.setLocation(persistentLocation);

		for (PurchaseOrderDetail detail : details) {
			detail.setUpdater(purchaseOrder.getUpdater());

			Long productId = detail.getProduct().getId();
			short receivedQuantity = 0;
			if (productsCount.containsKey(productId)) {
				receivedQuantity = productsCount.get(productId);
			}
			detail.setReceivedQuantity(receivedQuantity);
			detail.setRejectedQuantity((short) (detail.getOrderedQuantity() - receivedQuantity));
		}

		changeStatus(persistentPurchaseOrder, PurchaseOrderStatus.COMPLETE);

		this.getInventoryManager().add(persistentLocation.getId(), productsCount);

	}

	@Override
	public List<PurchaseOrderDetail> getDetails(Object purchaseOrderId) throws Exception {
		checkDependencies();
		return this.getDao().findById(purchaseOrderId).getDetails();
	}

	@Override
	protected void validate(PurchaseOrder purchaseOrder, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			final List<PurchaseOrderDetail> details = purchaseOrder.getDetails();
			if (details == null || details.isEmpty()
					|| details.stream().filter(x -> x.getOrderedQuantity() > 0).count() == 0) {
				throw new PurchaseOrderDetailsNullOrEmptyException();
			}
			if (DaoUtil.isDuplicate(this.getDao(), "referenceNumber", purchaseOrder.getReferenceNumber(),
					purchaseOrder.getId(), operation)) {
				throw new DuplicatePurchaseOrderReferenceNumberException(purchaseOrder.getReferenceNumber());
			}
			validateVendor(purchaseOrder);
			if (operation == UpdateOperation.UPDATE) {
				PurchaseOrderStatus oldStatus = getPersistentStatus(purchaseOrder.getId());
				validatePersistentStatus(oldStatus, operation); // If no
																// exception is
																// thrown at
																// this point,
																// then the old
																// status is
																// PENDING.
			}
			break;
		case DELETE:
			PurchaseOrderStatus oldStatus = getPersistentStatus(purchaseOrder.getId());
			validatePersistentStatus(oldStatus, operation);
			break;
		default:
			break;
		}
	}

	private void validateVendor(PurchaseOrder purchaseOrder) throws Exception {
		final Vendor vendor = purchaseOrder.getVendor();
		if (vendor != null && vendor.getId() != null) {
			Vendor persistentVendor = this.getVendorDao().findById(vendor.getId());

			if (!persistentVendor.isActive() && purchaseOrder.getStatus() != PurchaseOrderStatus.REJECTED) {
				throw new VendorInactiveException();
			}
		}
	}

	private void validatePersistentStatus(PurchaseOrderStatus status, UpdateOperation operation)
			throws PurchaseOrderReadOnlyException {
		if (operation == UpdateOperation.UPDATE || operation == UpdateOperation.DELETE) {
			if (status != null && status != PurchaseOrderStatus.PENDING) {
				throw new PurchaseOrderReadOnlyException(status);
			}
		}
	}

	private PurchaseOrderStatus getPersistentStatus(Long purchaseOrderId) throws Exception {
		PurchaseOrder purchaseOrder = this.getDao().findById(purchaseOrderId);
		if (purchaseOrder != null) {
			return purchaseOrder.getStatus();
		}
		return null;
	}

	private void changeStatus(PurchaseOrder purchaseOrder, PurchaseOrderStatus newStatus) throws Exception {
		PurchaseOrderStatus oldStatus = purchaseOrder.getStatus();
		validateTransition(oldStatus, newStatus);
		purchaseOrder.setStatus(newStatus);
		validateVendor(purchaseOrder);

		this.getDao().update(purchaseOrder);
	}

	private void validateTransition(PurchaseOrderStatus oldStatus, PurchaseOrderStatus newStatus)
			throws PurchaseOrderStatusTransitionException {
		if (oldStatus == PurchaseOrderStatus.PENDING) {
			if (newStatus == PurchaseOrderStatus.PENDING || newStatus == PurchaseOrderStatus.APPROVED
					|| newStatus == PurchaseOrderStatus.REJECTED) {
				return;
			}
		} else if (oldStatus == PurchaseOrderStatus.APPROVED) {
			if (newStatus == PurchaseOrderStatus.COMPLETE) {
				return;
			}
		}
		throw new PurchaseOrderStatusTransitionException(oldStatus, newStatus);
	}

	private void validateComplete(PurchaseOrder purchaseOrder, Map<Long, Short> productsCount)
			throws PurchaseOrderCompleteQuantityOutOfRangeException {
		List<Long> productIds = new ArrayList<>(productsCount.keySet());
		List<PurchaseOrderDetail> details = purchaseOrder.getDetails();
		for (Long productId : productIds) {
			short receivedQuantity = productsCount.get(productId);
			short orderedQuantity = 0;
			for (PurchaseOrderDetail detail : details) {
				if (detail.getProduct().getId().equals(productId)) {
					orderedQuantity += detail.getOrderedQuantity();
				}
			}
			if (receivedQuantity < 0 || receivedQuantity > orderedQuantity) {
				throw new PurchaseOrderCompleteQuantityOutOfRangeException(purchaseOrder.getId(), productId,
						orderedQuantity, receivedQuantity);
			}
		}
	}

	@Override
	public void create(PurchaseOrder purchaseOrder) throws Exception {
		onCreate(purchaseOrder);
		super.create(purchaseOrder);
	}

	@Override
	public void create(List<PurchaseOrder> purchaseOrders) throws Exception {
		purchaseOrders.stream().forEach(x -> onCreate(x));
		super.create(purchaseOrders);
	}

	private void onCreate(PurchaseOrder purchaseOrder) {
		purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
		if (purchaseOrder.getDetails() != null) {
			purchaseOrder.getDetails().stream().forEach(x -> {
				x.setReceivedQuantity((short) 0);
				x.setRejectedQuantity((short) 0);
			});
		}
	}
}
