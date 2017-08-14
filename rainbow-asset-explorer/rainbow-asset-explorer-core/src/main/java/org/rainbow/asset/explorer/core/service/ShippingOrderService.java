package org.rainbow.asset.explorer.core.service;

import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.core.entities.ShippingOrder;
import org.rainbow.asset.explorer.core.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.core.persistence.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderDeliveredQuantityOutOfRangeException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderStatusTransitionException;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.core.service.Service;

public interface ShippingOrderService extends Service<ShippingOrder, Long, SearchOptions> {

	void approve(ShippingOrder shippingOrder) throws Exception;

	void reject(ShippingOrder shippingOrder) throws Exception;

	/**
	 * This method will decrement the inventory of the source location.
	 *
	 * @param shippingOrder
	 * @throws org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderStatusTransitionException
	 * @throws org.rainbow.asset.explorer.core.persistence.exceptions.InsufficientInventoryException
	 * @throws org.optimum.persistence.exceptions.NonexistentEntityException
	 */
	void transit(ShippingOrder shippingOrder)
			throws ShippingOrderStatusTransitionException, InsufficientInventoryException, NonexistentEntityException;

	/**
	 * This method will increment the inventory of the target location.
	 *
	 * @param shippingOrder
	 * @param productsCount
	 * @throws org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderStatusTransitionException
	 * @throws org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderDeliveredQuantityOutOfRangeException
	 * @throws org.optimum.persistence.exceptions.NonexistentEntityException
	 */
	void deliver(ShippingOrder shippingOrder, Map<Long, Short> productsCount)
			throws ShippingOrderStatusTransitionException, ShippingOrderDeliveredQuantityOutOfRangeException,
			NonexistentEntityException;

	/**
	 * This method will restitute the inventory of the source location.
	 *
	 * @param shippingOrder
	 * @throws org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderStatusTransitionException
	 * @throws org.optimum.persistence.exceptions.NonexistentEntityException
	 */
	void restitute(ShippingOrder shippingOrder)
			throws ShippingOrderStatusTransitionException, NonexistentEntityException;

	List<ShippingOrderDetail> getDetails(Long shippingOrderId) throws Exception;

}