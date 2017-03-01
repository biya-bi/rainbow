package org.rainbow.shopping.cart.service;

import org.rainbow.persistence.IDao;
import org.rainbow.service.Service;
import org.rainbow.shopping.cart.model.Category;

public class CategoryService extends Service<Category> {

	public CategoryService(IDao<Category> dao) {
		super(dao);
	}
}
