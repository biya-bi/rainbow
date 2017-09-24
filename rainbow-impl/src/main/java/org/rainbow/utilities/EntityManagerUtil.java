package org.rainbow.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rainbow.persistence.exceptions.NonexistentEntityException;

public class EntityManagerUtil {

	public static <TEntity> TEntity findById(EntityManager em, Class<TEntity> entityClass, Object id) {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		if (id == null) {
			throw new IllegalArgumentException("The id argument cannot be null.");
		}
		TEntity obj;
		try {
			obj = em.getReference(entityClass, id);
			PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
			// If the entity does not exist, an EntityNotFoundException
			// exception will be thrown by the below line.
			util.getIdentifier(obj);
		} catch (EntityNotFoundException e) {
			throw new NonexistentEntityException(
					String.format("No %s with ID '%s' was found.", entityClass.getSimpleName(), id), e);
		}
		return obj;
	}

	/**
	 * Attempts to load a persistent version of the supplied entity.
	 * 
	 * @param entityClass
	 *            the class of the entity to load.
	 * @param entity
	 *            the entity for which the persistent version is to be loaded.
	 * @return the persistent version of the supplied entity.
	 * @throws NonexistentEntityException
	 *             the exception thrown if no persistent version of the supplied
	 *             entity was found.
	 */
	public static <TEntity> TEntity find(EntityManager em, Class<? extends TEntity> entityClass, TEntity entity)
			throws NonexistentEntityException {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		if (entity == null) {
			throw new IllegalArgumentException("The entity argument cannot be null.");
		}
		try {

			PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
			Object id = util.getIdentifier(entity);
			TEntity persistentEntity = em.getReference(entityClass, id);

			util.getIdentifier(persistentEntity); // If the entity does not
													// exist, an
													// EntityNotFoundException
													// exception will be thrown
													// here.
			em.refresh(persistentEntity);

			return persistentEntity;
		} catch (EntityNotFoundException e) {
			throw new NonexistentEntityException("An attempt to use a non-existing entity was made.", e);
		}
	}

	public static <TEntity, TValue> boolean isDuplicate(EntityManager em, Class<TEntity> entityClass, TValue value,
			String valueAttributeName, String idAttributeName, Object id) {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));
		Predicate p1;
		if (value instanceof String) {
			p1 = cb.equal(cb.upper(rt.<String>get(valueAttributeName)), ((String) value).toUpperCase());
		} else {
			p1 = cb.equal(rt.<TValue>get(valueAttributeName), value);
		}
		Predicate p2 = null;
		if (id != null) {
			p2 = cb.notEqual(rt.get(idAttributeName), id);
		}
		Predicate p = p2 == null ? p1 : cb.and(p1, p2);
		cq.where(p);
		TypedQuery<Long> tq = em.createQuery(cq);
		return tq.getSingleResult() > 0;
	}

	@SuppressWarnings("unchecked")
	public static <TEntity, TValue> boolean isDuplicate(EntityManager em, Class<TEntity> entityClass,
			Map<String, Object> pathValuePairs, String idAttributeName, Object id) {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		if (pathValuePairs == null || pathValuePairs.isEmpty()) {
			throw new IllegalArgumentException("pathValuePairs map can neither be null nor empty.");
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));

		Expression<Boolean>[] expressions = new Expression[pathValuePairs.size()];

		Object value;
		int i = 0;
		for (String path : pathValuePairs.keySet()) {
			value = pathValuePairs.get(path);
			if (value instanceof String) {
				expressions[i++] = cb.equal(cb.upper(getPath(path, rt)), ((String) value).toUpperCase());
			} else {
				expressions[i++] = cb.equal(getPath(path, rt), value);
			}
		}

		Predicate p1 = null;
		if (id != null) {
			p1 = cb.notEqual(rt.get(idAttributeName), id);
		}

		Expression<Boolean> p = p1 == null ? and(cb, expressions) : cb.and(and(cb, expressions), p1);

		cq.where(p);
		TypedQuery<Long> tq = em.createQuery(cq);
		return tq.getSingleResult() > 0;
	}

	@SuppressWarnings("rawtypes")
	private static <T, U> Path getPath(String path, Root<T> rt) {
		String[] parts = path.split(Pattern.quote("."));
		if (parts != null && parts.length > 1) {
			Join p = null;
			for (int i = 0; i < parts.length - 1; i++) {
				if (p == null) {
					p = rt.join(parts[i]);
				} else {
					p = p.join(parts[i]);
				}
			}
			if (p != null) {
				return p.get(parts[parts.length - 1]);
			}
		}
		return rt.get(path);
	}

	private static Expression<Boolean> and(CriteriaBuilder cb,
			@SuppressWarnings("unchecked") Expression<Boolean>... expressions) {
		Expression<Boolean> e = null;
		for (Expression<Boolean> expression : expressions) {
			if (expression != null) {
				if (e == null) {
					e = expression;
				} else {
					e = cb.and(e, expression);
				}
			}
		}
		return e;
	}

	@SuppressWarnings("unchecked")
	public static <TEntity> boolean exists(EntityManager em, Class<TEntity> entityClass,
			Map<String, Object> pathValuePairs) {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		if (pathValuePairs == null || pathValuePairs.isEmpty()) {
			throw new IllegalArgumentException("pathValuePairs map can neither be null nor empty.");
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));

		Expression<Boolean>[] expressions = new Expression[pathValuePairs.size()];

		Object value;
		int i = 0;
		for (String path : pathValuePairs.keySet()) {
			value = pathValuePairs.get(path);
			if (value instanceof String) {
				expressions[i++] = cb.equal(cb.upper(getPath(path, rt)), ((String) value).toUpperCase());
			} else {
				expressions[i++] = cb.equal(getPath(path, rt), value);
			}
		}

		cq.where(and(cb, expressions));
		TypedQuery<Long> tq = em.createQuery(cq);
		return tq.getSingleResult() > 0;
	}

	public static <TEntity> boolean exists(EntityManager em, Class<TEntity> entityClass, String path, Object value) {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put(path, value);
		return exists(em, entityClass, pathValuePairs);
	}

	@SuppressWarnings("unchecked")
	public static <TEntity> TEntity find(EntityManager em, Class<TEntity> entityClass,
			Map<String, Object> pathValuePairs) {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		if (pathValuePairs == null || pathValuePairs.isEmpty()) {
			throw new IllegalArgumentException("pathValuePairs map can neither be null nor empty.");
		}
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TEntity> cq = cb.createQuery(entityClass);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(rt);

		Expression<Boolean>[] expressions = new Expression[pathValuePairs.size()];

		Object value;
		int i = 0;
		for (String path : pathValuePairs.keySet()) {
			value = pathValuePairs.get(path);
			if (value instanceof String) {
				expressions[i++] = cb.equal(cb.upper(getPath(path, rt)), ((String) value).toUpperCase());
			} else {
				expressions[i++] = cb.equal(getPath(path, rt), value);
			}
		}

		cq.where(and(cb, expressions));
		TypedQuery<TEntity> tq = em.createQuery(cq);
		// tq.getSingleResult() throws an exception when the entity does not
		// exists.
		List<TEntity> entities = tq.getResultList();
		if (entities != null && entities.size() == 1) {
			return entities.get(0);
		}
		return null;
	}

	public static <TEntity> TEntity find(EntityManager em, Class<TEntity> entityClass, String path, Object value) {
		if (em == null) {
			throw new IllegalArgumentException("The em argument cannot be null.");
		}
		if (entityClass == null) {
			throw new IllegalArgumentException("The entityClass argument cannot be null.");
		}
		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put(path, value);
		return find(em, entityClass, pathValuePairs);
	}

}
