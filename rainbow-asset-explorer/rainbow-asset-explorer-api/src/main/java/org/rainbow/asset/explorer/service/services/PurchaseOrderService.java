package org.rainbow.asset.explorer.service.services;

import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrder;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetail;

public interface PurchaseOrderService extends DocumentDetailsService<PurchaseOrder, PurchaseOrderDetail> {
	void approve(PurchaseOrder purchaseOrder) throws Exception;

	void reject(PurchaseOrder purchaseOrder) throws Exception;

	void complete(PurchaseOrder purchaseOrder, Location location, Map<Long, Short> productsCount) throws Exception;
}
