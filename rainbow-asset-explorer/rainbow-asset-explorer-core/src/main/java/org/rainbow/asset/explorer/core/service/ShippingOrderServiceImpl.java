package org.rainbow.asset.explorer.core.service;

import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.core.entities.ShippingOrder;
import org.rainbow.asset.explorer.core.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.core.persistence.dao.ShippingOrderDao;
import org.rainbow.asset.explorer.core.persistence.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderDeliveredQuantityOutOfRangeException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ShippingOrderStatusTransitionException;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;

public class ShippingOrderServiceImpl extends RainbowAssetExplorerServiceImpl<ShippingOrder, Long, SearchOptions>
		implements ShippingOrderService {

	public ShippingOrderServiceImpl(ShippingOrderDao dao) {
		super(dao);
	}

	@Override
	public void approve(ShippingOrder shippingOrder) throws Exception {
		((ShippingOrderDao) this.getDao()).approve(shippingOrder);
	}

	@Override
	public void reject(ShippingOrder shippingOrder) throws Exception {
		((ShippingOrderDao) this.getDao()).reject(shippingOrder);
	}

	@Override
	public void transit(ShippingOrder shippingOrder)
			throws ShippingOrderStatusTransitionException, InsufficientInventoryException, NonexistentEntityException {
		((ShippingOrderDao) this.getDao()).transit(shippingOrder);
	}

	@Override
	public void deliver(ShippingOrder shippingOrder, Map<Long, Short> productsCount)
			throws ShippingOrderStatusTransitionException, ShippingOrderDeliveredQuantityOutOfRangeException,
			NonexistentEntityException {
		((ShippingOrderDao) this.getDao()).deliver(shippingOrder, productsCount);
	}

	@Override
	public void restitute(ShippingOrder shippingOrder)
			throws ShippingOrderStatusTransitionException, NonexistentEntityException {
		((ShippingOrderDao) this.getDao()).restitute(shippingOrder);
	}

	@Override
	public List<ShippingOrderDetail> getDetails(Long shippingOrderId) throws Exception {
		return ((ShippingOrderDao) this.getDao()).getDetails(shippingOrderId);
	}
}
