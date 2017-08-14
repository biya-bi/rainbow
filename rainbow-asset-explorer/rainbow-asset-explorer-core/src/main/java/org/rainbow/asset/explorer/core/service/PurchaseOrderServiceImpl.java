package org.rainbow.asset.explorer.core.service;

import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.entities.PurchaseOrder;
import org.rainbow.asset.explorer.core.entities.PurchaseOrderDetail;
import org.rainbow.asset.explorer.core.persistence.dao.PurchaseOrderDao;
import org.rainbow.core.persistence.SearchOptions;

public class PurchaseOrderServiceImpl extends RainbowAssetExplorerServiceImpl<PurchaseOrder, Long, SearchOptions>
		implements PurchaseOrderService {

	public PurchaseOrderServiceImpl(PurchaseOrderDao dao) {
		super(dao);
	}

	@Override
	public void approve(PurchaseOrder purchaseOrder) throws Exception {
		((PurchaseOrderDao) this.getDao()).approve(purchaseOrder);
	}

	@Override
	public void reject(PurchaseOrder purchaseOrder) throws Exception {
		((PurchaseOrderDao) this.getDao()).reject(purchaseOrder);
	}

	@Override
	public void complete(PurchaseOrder purchaseOrder, Location location, Map<Long, Short> productsCount)
			throws Exception {
		((PurchaseOrderDao) this.getDao()).complete(purchaseOrder, location, productsCount);
	}

	@Override
	public List<PurchaseOrderDetail> getDetails(Long purchaseOrderId) throws Exception {
		// TODO Auto-generated method stub
		return ((PurchaseOrderDao) this.getDao()).getDetails(purchaseOrderId);
	}
}
