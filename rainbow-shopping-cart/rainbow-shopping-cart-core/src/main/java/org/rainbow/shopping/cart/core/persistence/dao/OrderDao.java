package org.rainbow.shopping.cart.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.shopping.cart.core.entities.Order;
import org.rainbow.shopping.cart.core.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class OrderDao extends DaoImpl<Order,Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public OrderDao() {
		super(Order.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
