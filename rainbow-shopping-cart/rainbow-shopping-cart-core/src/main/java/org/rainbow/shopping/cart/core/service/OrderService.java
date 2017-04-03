package org.rainbow.shopping.cart.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.shopping.cart.core.entities.Order;

public class OrderService extends RainbowShoppingCartService<Order, Long, SearchOptions> {

	public OrderService(Dao<Order, Long, SearchOptions> dao) {
		super(dao);
	}
}
