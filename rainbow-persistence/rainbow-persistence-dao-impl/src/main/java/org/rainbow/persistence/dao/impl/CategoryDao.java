package org.rainbow.persistence.dao.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.persistence.dao.impl.exceptions.DuplicateCategoryNameException;
import org.rainbow.persistence.dao.impl.exceptions.InvalidCategoryHierarchyException;
import org.rainbow.persistence.dao.impl.exceptions.NonexistentEntityException;
import org.rainbow.shopping.cart.model.Category;

@Pageable(attributeName = "id")
public class CategoryDao extends Dao<Category> {

	@PersistenceContext
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
}
