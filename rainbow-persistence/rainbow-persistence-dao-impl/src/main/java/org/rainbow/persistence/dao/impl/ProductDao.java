package org.rainbow.persistence.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.persistence.dao.impl.exceptions.DuplicateProductCodeException;
import org.rainbow.persistence.dao.impl.exceptions.DuplicateProductNameException;
import org.rainbow.shopping.cart.model.Product;

@Pageable(attributeName = "id")
public class ProductDao extends Dao<Product> {

	@PersistenceContext
	private EntityManager em;

	public ProductDao() {
		super(Product.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Product product, UpdateOperation operation)
			throws DuplicateProductCodeException, DuplicateProductNameException {
		switch (operation) {
		case CREATE:
			if (exists(product.getCode(), "code", null, null)) {
				throw new DuplicateProductCodeException(product.getCode());
			}
			if (exists(product.getName(), "name", null, null)) {
				throw new DuplicateProductNameException(product.getName());
			}
			break;
		case UPDATE:
			if (exists(product.getCode(), "code", "id", product.getId())) {
				throw new DuplicateProductCodeException(product.getCode());
			}
			if (exists(product.getName(), "name", "id", product.getId())) {
				throw new DuplicateProductNameException(product.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
