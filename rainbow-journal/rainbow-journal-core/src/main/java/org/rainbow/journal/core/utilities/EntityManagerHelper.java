package org.rainbow.journal.core.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;

public class EntityManagerHelper {
	private EntityManager em;

	public EntityManagerHelper(EntityManager em) {
		this.em = em;
	}

	public <TEntity> TEntity getReference(Class<TEntity> entityClass, Object id) {
		TEntity obj;
		try {
			obj = em.getReference(entityClass, id);
			// The below line is important so that the object be refreshed with
			// values from the database. We are doing this because we may need
			// the
			// properties available in the database.
			em.refresh(obj);
		} catch (EntityNotFoundException e) {
			throw new NonexistentEntityException("Entity not found.", e);
		}
		return obj;
	}

	public <TEntity, TValue> boolean isDuplicate(Class<TEntity> entityClass, TValue value, String valueAttributeName,
			String idAttributeName, Object id) {
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
	public <TEntity, TValue> boolean isDuplicate(Class<TEntity> entityClass, Map<String, Object> pathValuePairs,
			String idAttributeName, Object id) {
		if (pathValuePairs == null || pathValuePairs.isEmpty())
			throw new IllegalArgumentException("pathValuePairs map can neither be null nor empty.");

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
	private <T, U> Path getPath(String path, Root<T> rt) {
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

	private Expression<Boolean> and(CriteriaBuilder cb,
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
	public <TEntity> boolean exists(Class<TEntity> entityClass, Map<String, Object> pathValuePairs) {
		if (pathValuePairs == null || pathValuePairs.isEmpty())
			throw new IllegalArgumentException("pathValuePairs map can neither be null nor empty.");
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

	public <TEntity> boolean exists(Class<TEntity> entityClass, String path, Object value) {
		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put(path, value);
		return exists(entityClass, pathValuePairs);
	}

	@SuppressWarnings("unchecked")
	public <TEntity> TEntity find(Class<TEntity> entityClass, Map<String, Object> pathValuePairs) {
		if (pathValuePairs == null || pathValuePairs.isEmpty())
			throw new IllegalArgumentException("pathValuePairs map can neither be null nor empty.");
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

	public <TEntity> TEntity find(Class<TEntity> entityClass, String path, Object value) {
		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put(path, value);
		return find(entityClass, pathValuePairs);
	}

}
