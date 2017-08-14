package org.rainbow.asset.explorer.core.service;

import java.util.List;

import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.persistence.dao.ProductDao;
import org.rainbow.core.persistence.SearchOptions;

public class ProductServiceImpl extends RainbowAssetExplorerServiceImpl<Product, Long, SearchOptions>
		implements ProductService {

	public ProductServiceImpl(ProductDao dao) {
		super(dao);
	}

	@Override
	public List<Product> find(List<String> productNumbers) {
		return ((ProductDao) this.getDao()).find(productNumbers);
	}
}
