package org.rainbow.asset.explorer.core.persistence.dao;

import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.entities.PurchaseOrder;
import org.rainbow.asset.explorer.core.entities.PurchaseOrderDetail;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public interface PurchaseOrderDao extends Dao<PurchaseOrder, Long, SearchOptions> {

	void approve(PurchaseOrder purchaseOrder) throws Exception;

	void reject(PurchaseOrder purchaseOrder) throws Exception;

	void complete(PurchaseOrder purchaseOrder, Location location, Map<Long, Short> productsCount) throws Exception;

	List<PurchaseOrderDetail> getDetails(Long purchaseOrderId) throws Exception;

}