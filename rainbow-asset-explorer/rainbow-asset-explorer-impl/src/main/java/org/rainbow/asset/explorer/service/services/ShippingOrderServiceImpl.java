package org.rainbow.asset.explorer.service.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderStatus;
import org.rainbow.asset.explorer.persistence.dao.InventoryManager;
import org.rainbow.asset.explorer.service.exceptions.DuplicateShippingOrderReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderDeliveredQuantityOutOfRangeException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderLocationException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderReadOnlyException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderStatusTransitionException;
import org.rainbow.persistence.Dao;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class ShippingOrderServiceImpl extends ServiceImpl<ShippingOrder, Long, SearchOptions>
		implements ShippingOrderService {

	private Dao<Location, Long, SearchOptions> locationDao;
	private InventoryManager inventoryManager;

	public ShippingOrderServiceImpl() {
	}

	public Dao<Location, Long, SearchOptions> getLocationDao() {
		return locationDao;
	}

	public void setLocationDao(Dao<Location, Long, SearchOptions> locationDao) {
		this.locationDao = locationDao;
	}

	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}

	public void setInventoryManager(InventoryManager inventoryManager) {
		this.inventoryManager = inventoryManager;
	}

	@Override
	public void approve(ShippingOrder shippingOrder) throws Exception {
		checkDependencies();
		final ShippingOrder persistentPurchaseOrder = this.getDao().findById(shippingOrder.getId());
		persistentPurchaseOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentPurchaseOrder, ShippingOrderStatus.APPROVED);
	}

	@Override
	public void reject(ShippingOrder shippingOrder) throws Exception {
		checkDependencies();
		final ShippingOrder persistentPurchaseOrder = this.getDao().findById(shippingOrder.getId());
		persistentPurchaseOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentPurchaseOrder, ShippingOrderStatus.REJECTED);
	}

	@Override
	public void transit(ShippingOrder shippingOrder) throws Exception {
		checkDependencies();
		ShippingOrder persistentShippingOrder = this.getDao().findById(shippingOrder.getId());
		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.IN_TRANSIT);
		persistentShippingOrder.setShipDate(new Date());

		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		Map<Long, Short> productsCount = new HashMap<>();
		for (ShippingOrderDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!productsCount.containsKey(productId)) {
				productsCount.put(productId, (short) 0);
			}
			short shippedQuantity = detail.getShippedQuantity();
			if (shippedQuantity > 0) {
				productsCount.replace(productId, (short) (productsCount.get(productId) + shippedQuantity));
			}
		}

		this.getInventoryManager().substract(persistentShippingOrder.getSourceLocation().getId(), productsCount);
	}

	@Override
	public void deliver(ShippingOrder shippingOrder, Map<Long, Short> productsCount) throws Exception {
		checkDependencies();
		ShippingOrder persistentShippingOrder = this.getDao().findById(shippingOrder.getId());
		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.DELIVERED);
		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		persistentShippingOrder.setDetails(details);

		validateDelivery(persistentShippingOrder, productsCount);

		persistentShippingOrder.setDeliveryDate(new Date());

		for (ShippingOrderDetail detail : details) {
			detail.setUpdater(shippingOrder.getUpdater());

			Long productId = detail.getProduct().getId();
			short deliveredQuantity = 0;
			if (productsCount.containsKey(productId)) {
				deliveredQuantity = productsCount.get(productId);
			}
			detail.setReceivedQuantity(deliveredQuantity);
			detail.setRejectedQuantity((short) (detail.getShippedQuantity() - deliveredQuantity));
		}
		this.getDao().update(persistentShippingOrder);
		this.getInventoryManager().add(persistentShippingOrder.getTargetLocation().getId(), productsCount);
	}

	@Override
	public void restitute(ShippingOrder shippingOrder) throws Exception {
		checkDependencies();
		ShippingOrder persistentShippingOrder;

		persistentShippingOrder = this.getDao().findById(shippingOrder.getId());

		persistentShippingOrder.setUpdater(shippingOrder.getUpdater());
		changeStatus(persistentShippingOrder, ShippingOrderStatus.RESTITUTED);

		List<ShippingOrderDetail> details = getDetails(shippingOrder.getId());
		Map<Long, Short> productsCount = new HashMap<>();
		for (ShippingOrderDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!productsCount.containsKey(productId)) {
				productsCount.put(productId, (short) 0);
			}
			short shippedQuantity = detail.getShippedQuantity();
			if (shippedQuantity > 0) {
				productsCount.replace(productId, (short) (productsCount.get(productId) + shippedQuantity));
			}
		}

		this.getInventoryManager().add(persistentShippingOrder.getSourceLocation().getId(), productsCount);
	}

	@Override
	public List<ShippingOrderDetail> getDetails(Long shippingOrderId) throws Exception {
		checkDependencies();
		return this.getDao().findById(shippingOrderId).getDetails();
	}

	@Override
	protected void validate(ShippingOrder shippingOrder, UpdateOperation operation) throws Exception {
		final List<ShippingOrderDetail> details;
		switch (operation) {
		case CREATE:
		case UPDATE:
			details = shippingOrder.getDetails();
			if (details == null || details.isEmpty()
					|| details.stream().filter(x -> x.getShippedQuantity() > 0).count() == 0) {
				throw new ShippingOrderDetailsNullOrEmptyException();
			}
			if (DaoUtil.isDuplicate(this.getDao(), "referenceNumber", shippingOrder.getReferenceNumber(),
					shippingOrder.getId(), operation)) {
				throw new DuplicateShippingOrderReferenceNumberException(shippingOrder.getReferenceNumber());
			}
			validateLocations(shippingOrder);
			if (operation == UpdateOperation.UPDATE) {
				ShippingOrderStatus oldStatus = getPersistentStatus(shippingOrder.getId());
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
			ShippingOrderStatus oldStatus = getPersistentStatus(shippingOrder.getId());
			validatePersistentStatus(oldStatus, operation);
			break;
		default:
			break;
		}
	}

	private void validateLocations(ShippingOrder shippingOrder) throws Exception {
		final Location sourceLocation = shippingOrder.getSourceLocation();
		final Location targetLocation = shippingOrder.getTargetLocation();

		shippingOrder.setSourceLocation(this.getLocationDao().findById(sourceLocation.getId()));
		shippingOrder.setTargetLocation(this.getLocationDao().findById(targetLocation.getId()));
		if (sourceLocation.equals(targetLocation)) {
			throw new ShippingOrderLocationException(sourceLocation.getId());
		}
	}

	private void validatePersistentStatus(ShippingOrderStatus status, UpdateOperation operation)
			throws ShippingOrderReadOnlyException {
		if (operation == UpdateOperation.UPDATE || operation == UpdateOperation.DELETE) {
			if (status != null && status != ShippingOrderStatus.PENDING) {
				throw new ShippingOrderReadOnlyException(status);
			}
		}
	}

	private ShippingOrderStatus getPersistentStatus(Long shippingOrderId) throws Exception {
		ShippingOrder shippingOrder = this.getDao().findById(shippingOrderId);
		if (shippingOrder != null) {
			return shippingOrder.getStatus();
		}
		return null;
	}

	private void validateTransition(ShippingOrderStatus oldStatus, ShippingOrderStatus newStatus)
			throws ShippingOrderStatusTransitionException {
		if (null != oldStatus) {
			switch (oldStatus) {
			case PENDING:
				if (newStatus == ShippingOrderStatus.PENDING || newStatus == ShippingOrderStatus.APPROVED
						|| newStatus == ShippingOrderStatus.REJECTED) {
					return;
				}
				break;
			case APPROVED:
				if (newStatus == ShippingOrderStatus.IN_TRANSIT) {
					return;
				}
				break;
			case IN_TRANSIT:
				if (newStatus == ShippingOrderStatus.DELIVERED || newStatus == ShippingOrderStatus.RESTITUTED) {
					return;
				}
				break;
			default:
				break;
			}
		}
		throw new ShippingOrderStatusTransitionException(oldStatus, newStatus);
	}

	private void changeStatus(ShippingOrder shippingOrder, ShippingOrderStatus newStatus) throws Exception {
		ShippingOrderStatus oldStatus = shippingOrder.getStatus();
		validateTransition(oldStatus, newStatus);
		shippingOrder.setStatus(newStatus);
		this.getDao().update(shippingOrder);
	}

	private void validateDelivery(ShippingOrder shippingOrder, Map<Long, Short> productsCount)
			throws ShippingOrderDeliveredQuantityOutOfRangeException {
		List<Long> productIds = new ArrayList<>(productsCount.keySet());
		List<ShippingOrderDetail> details = shippingOrder.getDetails();
		for (Long productId : productIds) {
			Short deliveredQuantity = productsCount.get(productId);
			short shippedQuantity = 0;
			for (ShippingOrderDetail detail : details) {
				if (detail.getProduct().getId().equals(productId)) {
					shippedQuantity += detail.getShippedQuantity();
				}
			}
			if (deliveredQuantity < 0 || deliveredQuantity > shippedQuantity) {
				throw new ShippingOrderDeliveredQuantityOutOfRangeException(shippingOrder.getId(), productId,
						shippedQuantity, deliveredQuantity);
			}
		}
	}

	@Override
	public void create(ShippingOrder shippingOrder) throws Exception {
		onCreate(shippingOrder);
		super.create(shippingOrder);
	}

	@Override
	public void create(List<ShippingOrder> shippingOrders) throws Exception {
		shippingOrders.stream().forEach(x -> onCreate(x));
		super.create(shippingOrders);
	}

	private void onCreate(ShippingOrder shippingOrder) {
		shippingOrder.setStatus(ShippingOrderStatus.PENDING);
		shippingOrder.setShipDate(null);
		if (shippingOrder.getDetails() != null) {
			shippingOrder.getDetails().stream().forEach(x -> {
				x.setReceivedQuantity((short) 0);
				x.setRejectedQuantity((short) 0);
			});
		}
	}

}
