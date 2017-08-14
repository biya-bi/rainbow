package org.rainbow.asset.explorer.core.service;

import java.util.List;

import org.rainbow.asset.explorer.core.entities.ProductReceipt;
import org.rainbow.asset.explorer.core.entities.ProductReceiptDetail;
import org.rainbow.asset.explorer.core.persistence.dao.ProductReceiptDao;
import org.rainbow.core.persistence.SearchOptions;

public class ProductReceiptServiceImpl extends RainbowAssetExplorerServiceImpl<ProductReceipt, Long, SearchOptions>
		implements ProductReceiptService {

	public ProductReceiptServiceImpl(ProductReceiptDao dao) {
		super(dao);
	}

	@Override
	public List<ProductReceiptDetail> getDetails(Long productReceiptId) {
		return ((ProductReceiptDao) this.getDao()).getDetails(productReceiptId);
	}
}
