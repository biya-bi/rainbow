package org.rainbow.service.api.impl;

import org.rainbow.persistence.dao.IDao;
import org.rainbow.shopping.cart.model.Product;

public class ProductService extends Service<Product> {

	public ProductService(IDao<Product> dao) {
		super(dao);
	}
}
