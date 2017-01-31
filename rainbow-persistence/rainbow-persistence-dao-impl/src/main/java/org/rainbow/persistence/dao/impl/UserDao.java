package org.rainbow.persistence.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.shopping.cart.model.User;

@Pageable(attributeName = "id")
public class UserDao extends Dao<User> {

	@PersistenceContext
	private EntityManager em;

	public UserDao() {
		super(User.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

}
