/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.core.persistence;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class DaoImpl<TEntity extends Object, TKey extends Serializable>
		implements Dao<TEntity, TKey, SearchOptions> {

	private final Class<TEntity> entityClass;

	public DaoImpl(Class<TEntity> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	protected void validate(TEntity entity, UpdateOperation operation) throws Exception {
	}

	@Override
	public void create(TEntity entity) throws Exception {
		validate(entity, UpdateOperation.CREATE);
		persist(entity);
	}

	@Override
	public void create(Collection<TEntity> entities) throws Exception {
		for (TEntity entity : entities) {
			validate(entity, UpdateOperation.CREATE);
			persist(entity);
		}
	}

	protected void persist(TEntity entity) {
		getEntityManager().persist(entity);
	}

	@Override
	public void update(TEntity entity) throws Exception {
		validate(entity, UpdateOperation.UPDATE);
		merge(entity);
	}

	@Override
	public void update(Collection<TEntity> entities) throws Exception {
		for (TEntity entity : entities) {
			validate(entity, UpdateOperation.UPDATE);
			merge(entity);
		}
	}

	protected void merge(TEntity entity) {
		getEntityManager().merge(entity);
	}

	@Override
	public void delete(TEntity entity) throws Exception {
		validate(entity, UpdateOperation.DELETE);
		remove(entity);
	}

	@Override
	public void delete(Collection<TEntity> entities) throws Exception {
		EntityManager em = getEntityManager();
		PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();

		Map<Object, TEntity> entitiesByIds = new HashMap<>();

		for (TEntity entity : entities) {
			validate(entity, UpdateOperation.DELETE);
			Object id = util.getIdentifier(entity);
			if (!entitiesByIds.containsKey(id)) {
				entitiesByIds.put(id, entity);
			}
		}

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TEntity> cq = cb.createQuery(entityClass);
		Root<TEntity> rt = cq.from(entityClass);

		Expression<Boolean> exp = rt.get(rt.getModel().getId(entityClass)).in(entitiesByIds.keySet());

		cq = cq.select(rt).where(exp);

		TypedQuery<TEntity> query = em.createQuery(cq);

		List<TEntity> persistentEntities = query.getResultList();

		for (TEntity entity : entities) {
			if (!persistentEntities.contains(entity))
				throw new NonexistentEntityException(
						String.format("The object %s cannot be deleted because it was not found.", entity));
		}

		for (TEntity persistentEntity : persistentEntities) {
			Object id = util.getIdentifier(persistentEntity);
			if (entitiesByIds.containsKey(id)) {
				onDelete(entitiesByIds.get(id), persistentEntity);
				em.remove(persistentEntity);
			}
		}
	}

	protected void onDelete(TEntity entity, TEntity persistentEntity) {
	}

	protected void remove(TEntity entity) throws NonexistentEntityException {
		TEntity persistentEntity = getPersistent(entity);
		getEntityManager().refresh(persistentEntity);
		onDelete(entity, persistentEntity);
		getEntityManager().remove(persistentEntity);
	}

	public TEntity getPersistent(TEntity entity) throws NonexistentEntityException {
		try {
			EntityManager em = getEntityManager();

			PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();
			Object id = util.getIdentifier(entity);
			TEntity persistentEntity = em.getReference(entityClass, id);

			util.getIdentifier(persistentEntity); // If the entity does not
													// exist, an
													// EntityNotFoundException
													// exception will be thrown
													// here.

			return persistentEntity;
		} catch (EntityNotFoundException e) {
			throw new NonexistentEntityException("An attempt to use a non-existing entity was made.", e);
		}
	}

	@Override
	public TEntity findById(TKey id) {
		return getEntityManager().find(entityClass, id);
	}

	@Override
	public List<TEntity> findAll() {
		CriteriaQuery<TEntity> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	@Override
	public List<TEntity> find(SearchOptions options) {
		Pageable[] annotations = this.getClass().getAnnotationsByType(Pageable.class);

		if (annotations.length == 0) {
			throw new UnsupportedOperationException(
					String.format("The class '%s' is not annotated with an annotation of type '%s'.", this.getClass(),
							Pageable.class));
		}

		String attributeName = annotations[0].attributeName();

		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TEntity> cq = cb.createQuery(entityClass);
		Root<TEntity> rt = cq.from(entityClass);

		Expression<?> exp = rt.get(attributeName);

		Expression<Boolean> e = null;

		List<Filter<?>> filters = options.getFilters();
		if (filters != null && !filters.isEmpty()) {
			e = getExpression(filters, cb, rt);
		}

		if (e != null) {
			cq = cq.select(rt).where(e);
		} else {
			cq = cq.select(rt);
		}
		cq = cq.orderBy(cb.asc(exp));

		TypedQuery<TEntity> query = getEntityManager().createQuery(cq);
		if (options.getFilters() != null && !options.getFilters().isEmpty()) {
			query = setParameters(options.getFilters(), query);
		}

		if (options.getPageIndex() != null && options.getPageSize() != null) {
			query.setFirstResult(options.getPageIndex() * options.getPageSize());
		}
		if (options.getPageSize() != null) {
			query.setMaxResults(options.getPageSize());
		}

		Logger.getLogger(this.getClass().getName()).fine(query.toString());
		return query.getResultList();
	}

	@Override
	public long count(SearchOptions options) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));
		Expression<Boolean> e = null;

		List<Filter<?>> filters = options.getFilters();
		if (filters != null && !filters.isEmpty()) {
			e = getExpression(filters, cb, rt);
		}

		if (e != null) {
			cq.where(e);
		}
		TypedQuery<Long> q = getEntityManager().createQuery(cq);
		if (options.getFilters() != null && options.getFilters().size() > 0) {
			q = setParameters(options.getFilters(), q);
		}
		return q.getSingleResult();
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <U> Expression<Boolean> getExpression(List<Filter<?>> filters, CriteriaBuilder cb, Root<TEntity> rt) {
		List<Expression<Boolean>> expressions = new ArrayList<>();
		for (Filter<?> filter : filters) {
			String parameterName = getParameterName(filter, filters);
			RelationalOperator operator = filter.getOperator();
			if (operator == null) {
				// If the operator is not specified, we ignore the value.
				continue;
			}
			switch (operator) {
			case EQUAL: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable<?> value = ((SingleValuedFilter<?>) filter).getValue();
				if (value == null)
					expressions.add(cb.isNull(getPath(filter, rt)));
				else if (value instanceof String)
					expressions.add(
							cb.equal(cb.upper(getPath(filter, rt)), cb.parameter(value.getClass(), parameterName)));
				else if (value instanceof Date) {
					Path path = getPath(filter, rt);
					Predicate e1 = cb.greaterThanOrEqualTo(path, getDay((Date) value, 0));
					Predicate e2 = cb.lessThan(path, getDay((Date) value, 1));
					expressions.add(cb.and(e1, e2));
				} else if (value instanceof Boolean) {
					if ((Boolean) value)
						expressions.add(cb.isTrue(getPath(filter, rt)));
					else
						expressions.add(cb.isFalse(getPath(filter, rt)));
				} else
					expressions.add(cb.equal(getPath(filter, rt), cb.parameter(value.getClass(), parameterName)));
				break;
			}
			case IS_EMPTY: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				expressions.add(cb.isNull(getPath(filter, rt)));
				break;
			}
			case IS_NOT_EMPTY: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				expressions.add(cb.isNotNull(getPath(filter, rt)));
				break;
			}
			case NOT_EQUAL: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable value = ((SingleValuedFilter) filter).getValue();
				if (value == null)
					expressions.add(cb.isNotNull(getPath(filter, rt)));
				else if (value instanceof String)
					expressions.add(
							cb.notEqual(cb.upper(getPath(filter, rt)), cb.parameter(value.getClass(), parameterName)));
				else if (value instanceof Date) {
					Path path = getPath(filter, rt);
					Predicate e1 = cb.lessThan(path, getDay((Date) value, 0));
					Predicate e2 = cb.greaterThanOrEqualTo(path, getDay((Date) value, 1));
					Predicate e3 = cb.isNull(path);
					expressions.add(cb.or(e1, e2, e3));
				} else if (value instanceof Boolean) {
					Path path = getPath(filter, rt);
					if ((Boolean) value)
						expressions.add(cb.or(cb.isFalse(path), cb.isNull(path)));
					else
						expressions.add(cb.or(cb.isTrue(path), cb.isNull(path)));
				} else
					expressions.add(cb.notEqual(getPath(filter, rt), cb.parameter(value.getClass(), parameterName)));
				break;
			}
			case LESS_THAN: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable value = ((SingleValuedFilter) filter).getValue();
				if (value == null)
					continue;
				else if (value instanceof String)
					expressions
							.add(cb.lessThan(cb.upper(getPath(filter, rt)), cb.parameter(String.class, parameterName)));
				else if (value instanceof Date)
					expressions.add(cb.lessThanOrEqualTo(getPath(filter, rt), getDay((Date) value, -1)));
				else
					expressions.add(cb.lessThan(getPath(filter, rt), cb.parameter(value.getClass(), parameterName)));
				break;
			}
			case LESS_THAN_OR_EQUAL: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable value = ((SingleValuedFilter) filter).getValue();
				if (value == null)
					continue;
				else if (value instanceof String)
					expressions.add(cb.lessThanOrEqualTo(cb.upper(getPath(filter, rt)),
							cb.parameter(String.class, parameterName)));
				else if (value instanceof Date) {
					Path path = getPath(filter, rt);
					Predicate e1 = cb.greaterThanOrEqualTo(path, getDay((Date) value, 0));
					Predicate e2 = cb.lessThan(path, getDay((Date) value, 1));
					Predicate e3 = cb.lessThan(path, cb.parameter(Date.class, parameterName));
					expressions.add(cb.or(e3, cb.and(e1, e2)));
				} else
					expressions.add(
							cb.lessThanOrEqualTo(getPath(filter, rt), cb.parameter(value.getClass(), parameterName)));
				break;
			}
			case GREATER_THAN: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable value = ((SingleValuedFilter) filter).getValue();
				if (value == null)
					continue;
				else if (value instanceof String)
					expressions.add(
							cb.greaterThan(cb.upper(getPath(filter, rt)), cb.parameter(String.class, parameterName)));
				else if (value instanceof Date)
					expressions.add(cb.greaterThanOrEqualTo(getPath(filter, rt), getDay((Date) value, 1)));
				else
					expressions.add(cb.greaterThan(getPath(filter, rt), cb.parameter(value.getClass(), parameterName)));
				break;
			}
			case GREATER_THAN_OR_EQUAL: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable value = ((SingleValuedFilter) filter).getValue();
				if (value == null)
					continue;
				else if (value instanceof String)
					expressions.add(cb.greaterThanOrEqualTo(cb.upper(getPath(filter, rt)),
							cb.parameter(String.class, parameterName)));
				else if (value instanceof Date) {
					Path path = getPath(filter, rt);
					Predicate e1 = cb.greaterThanOrEqualTo(path, getDay((Date) value, 0));
					Predicate e2 = cb.lessThan(path, getDay((Date) value, 1));
					Predicate e3 = cb.greaterThan(path, cb.parameter(Date.class, parameterName));
					expressions.add(cb.or(e3, cb.and(e1, e2)));
				} else
					expressions.add(cb.greaterThanOrEqualTo(getPath(filter, rt),
							cb.parameter(value.getClass(), parameterName)));
				break;
			}
			case CONTAINS:
			case STARTS_WITH:
			case ENDS_WITH: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable value = ((SingleValuedFilter) filter).getValue();
				if (value == null)
					continue;
				if (value instanceof String && !((String) value).isEmpty())
					expressions.add(cb.like(cb.upper(getPath(filter, rt)), cb.parameter(String.class, parameterName)));
				break;
			}
			case DOES_NOT_CONTAIN: {
				if (!(filter instanceof SingleValuedFilter))
					continue;
				Comparable value = ((SingleValuedFilter) filter).getValue();
				if (value == null)
					continue;
				if (value instanceof String && !((String) value).isEmpty())
					expressions
							.add(cb.notLike(cb.upper(getPath(filter, rt)), cb.parameter(String.class, parameterName)));
				break;
			}
			case IS_BETWEEN: {
				if (!(filter instanceof RangeValuedFilter))
					continue;
				RangeValuedFilter<?> rangeValuedFilter = ((RangeValuedFilter<?>) filter);
				Comparable lowerBound = rangeValuedFilter.getLowerBound();
				Comparable upperBound = rangeValuedFilter.getUpperBound();
				if (lowerBound == null || upperBound == null)
					continue;
				Path path = getPath(filter, rt);

				if (rangeValuedFilter.getLowerBound() instanceof Date) {
					expressions.add(cb.and(cb.greaterThanOrEqualTo(path, getDay((Date) lowerBound, 0)),
							cb.lessThan(path, getDay((Date) upperBound, 1))));
				} else
					expressions.add(
							cb.and(cb.greaterThanOrEqualTo(path, lowerBound), cb.lessThanOrEqualTo(path, upperBound)));
				break;
			}
			case IS_NOT_BETWEEN: {
				if (!(filter instanceof RangeValuedFilter))
					continue;
				RangeValuedFilter<?> rangeValuedFilter = ((RangeValuedFilter<?>) filter);
				Comparable lowerBound = rangeValuedFilter.getLowerBound();
				Comparable upperBound = rangeValuedFilter.getUpperBound();
				if (lowerBound == null || upperBound == null)
					continue;
				Path path = getPath(filter, rt);

				if (rangeValuedFilter.getLowerBound() instanceof Date) {
					expressions.add(cb.not(cb.and(cb.greaterThanOrEqualTo(path, getDay((Date) lowerBound, 0)),
							cb.lessThan(path, getDay((Date) upperBound, 1)))));
				} else
					expressions.add(cb.not(
							cb.and(cb.greaterThanOrEqualTo(path, lowerBound), cb.lessThanOrEqualTo(path, upperBound))));
				break;
			}
			case IN: {
				if (!(filter instanceof ListValuedFilter))
					continue;
				ListValuedFilter<?> listValuedFilter = ((ListValuedFilter<?>) filter);
				List<?> list = listValuedFilter.getList();
				if (list == null || list.isEmpty())
					continue;
				Path path = getPath(filter, rt);

				List<String> upperCaseStrings = new ArrayList<>();
				for (Object o : list) {
					if (o != null && o instanceof String) {
						upperCaseStrings.add(((String) o).toUpperCase());
					}
				}
				if (!upperCaseStrings.isEmpty())
					expressions.add(cb.upper(path).in(upperCaseStrings));
				else
					expressions.add(path.in(list));
				break;
			}
			case NOT_IN: {
				if (!(filter instanceof ListValuedFilter))
					continue;
				ListValuedFilter<?> listValuedFilter = ((ListValuedFilter<?>) filter);
				List<?> list = listValuedFilter.getList();
				if (list == null || list.isEmpty())
					continue;
				Path path = getPath(filter, rt);

				List<String> upperCaseStrings = new ArrayList<>();
				for (Object o : list) {
					if (o != null && o instanceof String) {
						upperCaseStrings.add(((String) o).toUpperCase());
					}
				}
				if (!upperCaseStrings.isEmpty())
					expressions.add(cb.not(cb.upper(path).in(upperCaseStrings)));
				else
					expressions.add(cb.not(path.in(list)));
				break;
			}
			}
		}
		if (expressions.isEmpty())
			return null;
		Expression[] expressionsArray = new Expression[expressions.size()];
		return and(cb, expressions.toArray(expressionsArray));
	}

	private <U> TypedQuery<U> setParameters(List<Filter<?>> filters, TypedQuery<U> query) {
		for (Filter<?> filter : filters)
			query = setParameter(filter, filters, query);
		return query;
	}

	public long count() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));
		TypedQuery<Long> q = getEntityManager().createQuery(cq);
		return q.getSingleResult();
	}

	private String getParameterName(Filter<?> filter, List<Filter<?>> filters) {
		return filter.getFieldName().replace('.', '_').toUpperCase() + "_PARAM_" + filters.indexOf(filter);
	}

	private <U> TypedQuery<U> setParameter(Filter<?> filter, List<Filter<?>> filters, TypedQuery<U> query) {
		RelationalOperator operator = filter.getOperator();
		if (null != operator) {
			switch (operator) {
			case EQUAL:
				if (filter instanceof SingleValuedFilter) {
					Comparable<?> value = ((SingleValuedFilter<?>) filter).getValue();
					if (value != null) {
						if (value instanceof String)
							query.setParameter(getParameterName(filter, filters),
									String.valueOf(value).trim().toUpperCase());
						// We have already taken care of the Boolean case in
						// the getExpression method.
						else if (!(value instanceof Boolean))
							query.setParameter(getParameterName(filter, filters), value);

					}
				}
				break;
			case CONTAINS:
			case DOES_NOT_CONTAIN:
				if (filter instanceof SingleValuedFilter) {
					Comparable<?> value = ((SingleValuedFilter<?>) filter).getValue();
					if (value != null) {
						if (value instanceof String && !((String) value).isEmpty())
							query.setParameter(getParameterName(filter, filters),
									"%" + String.valueOf(value).trim().toUpperCase() + "%");

					}
				}
				break;
			case ENDS_WITH:
				if (filter instanceof SingleValuedFilter) {
					Comparable<?> value = ((SingleValuedFilter<?>) filter).getValue();
					if (value != null) {
						if (value instanceof String && !((String) value).isEmpty())
							query.setParameter(getParameterName(filter, filters),
									"%" + String.valueOf(value).trim().toUpperCase());

					}
				}
				break;
			case NOT_EQUAL:
				if (filter instanceof SingleValuedFilter) {
					Comparable<?> value = ((SingleValuedFilter<?>) filter).getValue();
					if (value != null) {
						if (value instanceof String)
							query.setParameter(getParameterName(filter, filters),
									String.valueOf(value).trim().toUpperCase());
						else
							query.setParameter(getParameterName(filter, filters), value);

					}
				}
				break;
			case STARTS_WITH:
				if (filter instanceof SingleValuedFilter) {
					Comparable<?> value = ((SingleValuedFilter<?>) filter).getValue();
					if (value != null) {
						if (value instanceof String && !((String) value).isEmpty())
							query.setParameter(getParameterName(filter, filters),
									String.valueOf(value).trim().toUpperCase() + "%");

					}
				}
				break;
			case LESS_THAN:
			case LESS_THAN_OR_EQUAL:
			case GREATER_THAN:
			case GREATER_THAN_OR_EQUAL:
				if (filter instanceof SingleValuedFilter) {
					Comparable<?> value = ((SingleValuedFilter<?>) filter).getValue();
					if (value != null) {
						if (value instanceof String)
							query.setParameter(getParameterName(filter, filters),
									String.valueOf(value).trim().toUpperCase());
						else
							query.setParameter(getParameterName(filter, filters), value);

					}
				}
				break;
			default:
				break;
			}
		}
		return query;
	}

	@SuppressWarnings("rawtypes")
	private <U> Path getPath(Filter<U> filter, Root<TEntity> rt) {
		String[] parts = filter.getFieldName().split(Pattern.quote("."));
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
		return rt.get(filter.getFieldName());
	}

	private Date getDay(Date date, int amount) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		c.add(Calendar.DATE, amount);
		return c.getTime();
	}

	protected boolean exists(String value, String valueAttributeName, String idAttributeName, Object id) {
		EntityManager em = getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));
		Predicate p1 = cb.equal(cb.upper(rt.<String>get(valueAttributeName)), value.toUpperCase());
		Predicate p2 = null;
		if (id != null) {
			p2 = cb.notEqual(rt.get(idAttributeName), id);
		}
		Predicate p = p2 == null ? p1 : cb.and(p1, p2);
		cq.where(p);
		TypedQuery<Long> tq = em.createQuery(cq);
		return tq.getSingleResult() > 0;
	}

	public <U> boolean exists(U value, String valueAttributeName) {
		EntityManager em = getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));
		Predicate p;
		if (value instanceof String) {
			p = cb.equal(cb.upper(rt.<String>get(valueAttributeName)), ((String) value).toUpperCase());
		} else {
			p = cb.equal(rt.<U>get(valueAttributeName), value);
		}
		cq.where(p);
		TypedQuery<Long> tq = em.createQuery(cq);
		return tq.getSingleResult() > 0;
	}
}
