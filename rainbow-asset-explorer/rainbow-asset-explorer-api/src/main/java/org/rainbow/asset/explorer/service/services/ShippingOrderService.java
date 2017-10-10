package org.rainbow.asset.explorer.service.services;

import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderDetail;

public interface ShippingOrderService extends DocumentDetailsService<ShippingOrder, ShippingOrderDetail> {

	void approve(ShippingOrder shippingOrder) throws Exception;

	void reject(ShippingOrder shippingOrder) throws Exception;

	void transit(ShippingOrder shippingOrder) throws Exception;

	void deliver(ShippingOrder shippingOrder, Map<Long, Short> productsCount) throws Exception;

	void restitute(ShippingOrder shippingOrder) throws Exception;
}
