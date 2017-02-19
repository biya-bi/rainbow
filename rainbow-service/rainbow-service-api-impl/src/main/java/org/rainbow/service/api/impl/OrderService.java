package org.rainbow.service.api.impl;

import org.rainbow.persistence.dao.IDao;
import org.rainbow.shopping.cart.model.Order;

public class OrderService extends Service<Order> {

	public OrderService(IDao<Order> dao) {
		super(dao);
	}
}
