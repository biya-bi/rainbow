package org.rainbow.service.api.impl;

import org.rainbow.persistence.dao.IDao;
import org.rainbow.shopping.cart.model.Category;

public class CategoryService extends Service<Category> {

	public CategoryService(IDao<Category> dao) {
		super(dao);
	}
}
