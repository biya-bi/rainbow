package org.rainbow.shopping.cart.service;

import org.rainbow.persistence.IDao;
import org.rainbow.service.Service;
import org.rainbow.shopping.cart.model.Order;

public class OrderService extends Service<Order> {

	public OrderService(IDao<Order> dao) {
		super(dao);
	}
}
