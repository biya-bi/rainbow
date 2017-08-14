package org.rainbow.asset.explorer.core.service;

import java.util.List;

import org.rainbow.asset.explorer.core.entities.ProductReceipt;
import org.rainbow.asset.explorer.core.entities.ProductReceiptDetail;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;

public interface ProductReceiptService extends Service<ProductReceipt, Long, SearchOptions> {

	List<ProductReceiptDetail> getDetails(Long productReceiptId);

}