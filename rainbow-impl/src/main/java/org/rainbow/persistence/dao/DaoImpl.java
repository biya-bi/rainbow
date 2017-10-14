package org.rainbow.persistence.dao;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
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
		this.entityClass = Objects.requireNonNull(entityClass);
	}

	protected abstract EntityManager getEntityManager();

	@Override
	public void create(TEntity entity) throws Exception {
		Objects.requireNonNull(entity);
		onCreate(entity);
		persist(entity);
	}

	protected void onCreate(TEntity entity) throws Exception {
	}

	@Override
	public void create(List<TEntity> entities) throws Exception {
		Objects.requireNonNull(entities);
		entities = ensureAtLeastOneNonNull(entities);
		for (TEntity entity : entities) {
			onCreate(entity);
			persist(entity);
		}
	}

	protected void persist(TEntity entity) {
		Objects.requireNonNull(entity);
		getEntityManager().persist(entity);
	}

	@Override
	public void update(TEntity entity) throws Exception {
		Objects.requireNonNull(entity);
		onUpdate(entity);
		merge(entity);
	}

	@Override
	public void update(List<TEntity> entities) throws Exception {
		Objects.requireNonNull(entities);
		entities = ensureAtLeastOneNonNull(entities);
		for (TEntity entity : entities) {
			onUpdate(entity);
			merge(entity);
		}
	}

	protected void onUpdate(TEntity entity) throws Exception {
	}

	protected void merge(TEntity entity) {
		Objects.requireNonNull(entity);
		getEntityManager().merge(entity);
	}

	@Override
	public void delete(TEntity entity) throws Exception {
		Objects.requireNonNull(entity);
		remove(entity);
	}

	protected void onDelete(TEntity entity) throws Exception {
	}

	@Override
	public void delete(List<TEntity> entities) throws Exception {
		Objects.requireNonNull(entities);

		entities = ensureAtLeastOneNonNull(entities);

		EntityManager em = getEntityManager();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<TEntity> cq = cb.createQuery(entityClass);
		Root<TEntity> rt = cq.from(entityClass);

		Predicate p = cb.equal(rt, entities.get(0));
		for (int i = 1; i < entities.size(); i++) {
			p = cb.or(p, cb.equal(rt, entities.get(i)));
		}

		cq = cq.select(rt).where(p);

		TypedQuery<TEntity> query = em.createQuery(cq);

		List<TEntity> persistentEntities = query.getResultList();

		for (TEntity entity : entities) {
			if (!persistentEntities.contains(entity))
				throw new NonexistentEntityException(
						String.format("The object %s cannot be deleted because it was not found.", entity));
		}

		for (TEntity persistentEntity : persistentEntities) {
			onDelete(entities.get(entities.indexOf(persistentEntity)), persistentEntity);
			em.remove(persistentEntity);
		}
	}

	private List<TEntity> ensureAtLeastOneNonNull(List<TEntity> entities) {
		entities = entities.stream().filter(x -> x != null).collect(Collectors.toList());
		if (entities.isEmpty()) {
			throw new IllegalArgumentException("The entities argument must contain at least one non null entity.");
		}
		return entities;
	}

	protected void onDelete(TEntity entity, TEntity persistentEntity) {
	}

	private void remove(TEntity entity) throws Exception {
		Objects.requireNonNull(entity);
		onDelete(entity);
		final EntityManager em = getEntityManager();
		TEntity persistentEntity = EntityManagerUtil.find(this.getEntityManager(), entityClass, entity);
		onDelete(entity, persistentEntity);
		em.remove(persistentEntity);
	}

	@Override
	public TEntity findById(Object id) throws Exception {
		Objects.requireNonNull(id);
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
		Objects.requireNonNull(searchOptions);
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

}
