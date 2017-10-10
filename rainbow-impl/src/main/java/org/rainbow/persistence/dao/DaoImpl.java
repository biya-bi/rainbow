package org.rainbow.persistence.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.rainbow.criteria.PredicateUtil;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.util.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class DaoImpl<TEntity extends Object> implements Dao<TEntity> {

	private final Class<TEntity> entityClass;

	public DaoImpl(Class<TEntity> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	@Override
	public void create(TEntity entity) throws Exception {
		onCreate(entity);
		persist(entity);
	}

	protected void onCreate(TEntity entity) throws Exception {
	}

	@Override
	public void create(List<TEntity> entities) throws Exception {
		for (TEntity entity : entities) {
			onCreate(entity);
			persist(entity);
		}
	}

	protected void persist(TEntity entity) {
		getEntityManager().persist(entity);
	}

	@Override
	public void update(TEntity entity) throws Exception {
		onUpdate(entity);
		merge(entity);
	}

	@Override
	public void update(List<TEntity> entities) throws Exception {
		for (TEntity entity : entities) {
			onUpdate(entity);
			merge(entity);
		}
	}

	protected void onUpdate(TEntity entity) throws Exception {
	}

	protected void merge(TEntity entity) {
		getEntityManager().merge(entity);
	}

	@Override
	public void delete(TEntity entity) throws Exception {
		onDelete(entity);
		remove(entity);
	}

	protected void onDelete(TEntity entity) throws Exception {
	}

	@Override
	public void delete(List<TEntity> entities) throws Exception {
		EntityManager em = getEntityManager();
		PersistenceUnitUtil util = em.getEntityManagerFactory().getPersistenceUnitUtil();

		Map<Object, TEntity> entitiesByIds = new HashMap<>();

		for (TEntity entity : entities) {
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

	protected void remove(TEntity entity) throws Exception {
		onDelete(entity);
		final EntityManager em = getEntityManager();
		TEntity persistentEntity = EntityManagerUtil.find(this.getEntityManager(), entityClass, entity);
		onDelete(entity, persistentEntity);
		em.remove(persistentEntity);
	}

	@Override
	public TEntity findById(Object id) throws Exception {
		return EntityManagerUtil.findById(this.getEntityManager(), entityClass, id);
	}

	@Override
	public List<TEntity> findAll() {
		CriteriaQuery<TEntity> cq = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
		cq.select(cq.from(entityClass));
		return getEntityManager().createQuery(cq).getResultList();
	}

	@Override
	public List<TEntity> find(SearchOptions searchOptions) {
		if (searchOptions == null) {
			throw new IllegalArgumentException("The searchOptions argument cannot be null.");
		}

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

		org.rainbow.criteria.Predicate predicate = searchOptions.getPredicate();
		if (predicate != null) {
			cq = cq.select(rt).where(PredicateUtil.map(predicate, cb, rt));
		} else {
			cq = cq.select(rt);
		}
		cq = cq.orderBy(cb.asc(exp));

		TypedQuery<TEntity> query = getEntityManager().createQuery(cq);

		if (searchOptions.getPageIndex() != null && searchOptions.getPageSize() != null) {
			query.setFirstResult(searchOptions.getPageIndex() * searchOptions.getPageSize());
		}
		if (searchOptions.getPageSize() != null) {
			query.setMaxResults(searchOptions.getPageSize());
		}

		Logger.getLogger(this.getClass().getName()).fine(query.toString());
		return query.getResultList();
	}

	@Override
	public long count(org.rainbow.criteria.Predicate predicate) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));

		if (predicate != null) {
			cq.where(PredicateUtil.map(predicate, cb, rt));
		}
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	public long count() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<TEntity> rt = cq.from(entityClass);
		cq.select(cb.count(rt));
		TypedQuery<Long> q = getEntityManager().createQuery(cq);
		return q.getSingleResult();
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
