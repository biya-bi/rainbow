package org.rainbow.shopping.cart.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.shopping.cart.core.entities.Product;

public class ProductService extends RainbowShoppingCartService<Product, Long, SearchOptions> {

	public ProductService(Dao<Product, Long, SearchOptions> dao) {
		super(dao);
	}
}
