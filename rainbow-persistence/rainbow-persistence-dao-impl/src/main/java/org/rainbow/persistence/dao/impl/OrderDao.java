package org.rainbow.persistence.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.shopping.cart.model.Order;

@Pageable(attributeName = "id")
public class OrderDao extends Dao<Order> {

	@PersistenceContext
	private EntityManager em;

	public OrderDao() {
		super(Order.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
