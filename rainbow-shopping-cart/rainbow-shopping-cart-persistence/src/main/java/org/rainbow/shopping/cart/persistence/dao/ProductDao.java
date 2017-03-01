package org.rainbow.shopping.cart.persistence.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.rainbow.shopping.cart.model.Photo;
import org.rainbow.shopping.cart.model.Product;
import org.rainbow.shopping.cart.persistence.exceptions.DuplicateProductCodeException;
import org.rainbow.shopping.cart.persistence.exceptions.DuplicateProductNameException;

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

	@Override
	public void update(Product entity) throws Exception {
		super.update(entity);
		if (entity.getPhoto() != null) {
			boolean photoExists = false;
			if (entity.getPhoto().getId() != null) {
				try {
					Photo persistentPhoto = em.getReference(Photo.class, entity.getPhoto().getId());
					if (persistentPhoto != null && persistentPhoto.getId().equals(entity.getPhoto().getId()))
						photoExists = true;
				} catch (EntityNotFoundException e) {
					Logger.getLogger(this.getClass().getName()).warning(entity.getPhoto() + " was not found.");
					System.out.println(entity.getPhoto() + " was not found.");
				}
			}
			if (!photoExists)
				em.persist(entity.getPhoto());
			else
				em.merge(entity.getPhoto());
		}
	}
}
