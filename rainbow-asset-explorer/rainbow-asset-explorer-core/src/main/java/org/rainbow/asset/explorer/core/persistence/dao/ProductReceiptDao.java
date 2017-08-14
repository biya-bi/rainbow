package org.rainbow.asset.explorer.core.persistence.dao;

import java.util.List;

import org.rainbow.asset.explorer.core.entities.ProductReceipt;
import org.rainbow.asset.explorer.core.entities.ProductReceiptDetail;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public interface ProductReceiptDao extends Dao<ProductReceipt, Long, SearchOptions> {

	List<ProductReceiptDetail> getDetails(Long productReceiptId);

}