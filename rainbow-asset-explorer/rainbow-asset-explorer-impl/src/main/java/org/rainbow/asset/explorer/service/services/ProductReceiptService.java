package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.ProductReceipt;
import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetail;
import org.rainbow.persistence.SearchOptions;

public interface ProductReceiptService
		extends DocumentDetailsService<ProductReceipt, Long, SearchOptions, ProductReceiptDetail> {
}