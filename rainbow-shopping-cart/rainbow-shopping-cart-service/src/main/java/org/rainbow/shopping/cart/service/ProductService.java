package org.rainbow.shopping.cart.service;

import org.rainbow.persistence.IDao;
import org.rainbow.service.Service;
import org.rainbow.shopping.cart.model.Product;

public class ProductService extends Service<Product> {

	public ProductService(IDao<Product> dao) {
		super(dao);
	}
}
