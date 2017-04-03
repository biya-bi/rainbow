package org.rainbow.shopping.cart.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.shopping.cart.core.entities.Category;

public class CategoryService extends RainbowShoppingCartService<Category, Long, SearchOptions> {

	public CategoryService(Dao<Category, Long, SearchOptions> dao) {
		super(dao);
	}
}
