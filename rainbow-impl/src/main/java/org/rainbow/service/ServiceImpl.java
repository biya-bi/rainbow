package org.rainbow.service;

import java.util.List;

import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.persistence.dao.Dao;
import org.rainbow.service.services.Service;

public class ServiceImpl<TEntity extends Object> implements Service<TEntity> {
	private Dao<TEntity> dao;

	public Dao<TEntity> getDao() {
		return dao;
	}

	public void setDao(Dao<TEntity> dao) {
		this.dao = dao;
	}

	@Override
	public void create(TEntity entity) throws Exception {
		checkDependencies();
		validate(entity, UpdateOperation.CREATE);
		this.getDao().create(entity);
	}

	@Override
	public void update(TEntity entity) throws Exception {
		checkDependencies();
		validate(entity, UpdateOperation.UPDATE);
		this.getDao().update(entity);
	}

	@Override
	public void delete(TEntity entity) throws Exception {
		checkDependencies();
		validate(entity, UpdateOperation.DELETE);
		this.getDao().delete(entity);
	}

	@Override
	public TEntity findById(Object id) throws Exception {
		checkDependencies();
		return this.getDao().findById(id);
	}

	@Override
	public List<TEntity> findAll() throws Exception {
		checkDependencies();
		return this.getDao().findAll();
	}

	@Override
	public List<TEntity> find(SearchOptions options) throws Exception {
		checkDependencies();
		return this.getDao().find(options);
	}

	@Override
	public long count(Predicate predicate) throws Exception {
		checkDependencies();
		return this.getDao().count(predicate);
	}

	@Override
	public void create(List<TEntity> entities) throws Exception {
		checkDependencies();
		for (TEntity entity : entities) {
			validate(entity, UpdateOperation.CREATE);
		}
		this.getDao().create(entities);
	}

	@Override
	public void update(List<TEntity> entities) throws Exception {
		checkDependencies();
		for (TEntity entity : entities) {
			validate(entity, UpdateOperation.UPDATE);
		}
		this.getDao().update(entities);
	}

	@Override
	public void delete(List<TEntity> entities) throws Exception {
		checkDependencies();
		for (TEntity entity : entities) {
			validate(entity, UpdateOperation.DELETE);
		}
		this.getDao().delete(entities);
	}

	protected void validate(TEntity entity, UpdateOperation operation) throws Exception {
	}

	protected void checkDependencies() {
		if (this.getDao() == null) {
			throw new IllegalStateException("The data access object cannot be null.");
		}
	}

}
