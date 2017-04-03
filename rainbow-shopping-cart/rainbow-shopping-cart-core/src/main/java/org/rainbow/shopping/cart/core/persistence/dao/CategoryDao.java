package org.rainbow.shopping.cart.core.persistence.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.shopping.cart.core.entities.Category;
import org.rainbow.shopping.cart.core.entities.Photo;
import org.rainbow.shopping.cart.core.persistence.exceptions.DuplicateCategoryNameException;
import org.rainbow.shopping.cart.core.persistence.exceptions.InvalidCategoryHierarchyException;
import org.rainbow.shopping.cart.core.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class CategoryDao extends DaoImpl<Category, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public CategoryDao() {
		super(Category.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Category category, UpdateOperation operation)
			throws DuplicateCategoryNameException, InvalidCategoryHierarchyException, NonexistentEntityException {
		switch (operation) {
		case CREATE:
			if (exists(category.getName(), "name", null, null)) {
				throw new DuplicateCategoryNameException(category.getName());
			}
			validateHierarchy(category);
			break;
		case UPDATE:
			if (exists(category.getName(), "name", "id", category.getId())) {
				throw new DuplicateCategoryNameException(category.getName());
			}
			validateHierarchy(category);
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	private boolean isDescendant(Category child, Category parent) {
		if (child.getParent() == null)
			return false;
		if (child.getParent().equals(parent))
			return true;
		if (child.getParent().getParent() == null)
			return false;
		return isDescendant(child.getParent(), parent);
	}

	private void validateHierarchy(Category category)
			throws InvalidCategoryHierarchyException, NonexistentEntityException {
		if (category.equals(category.getParent()))
			throw new InvalidCategoryHierarchyException();
		if (category.getParent() != null) {
			if (isDescendant(getPersistent(category.getParent()), category))
				throw new InvalidCategoryHierarchyException();
		}
	}

	@Override
	public void update(Category entity) throws Exception {
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
				}
			}
			if (!photoExists)
				em.persist(entity.getPhoto());
			else
				em.merge(entity.getPhoto());
		}
	}
}
