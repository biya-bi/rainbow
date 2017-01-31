package org.rainbow.service.api.impl;

import java.util.List;

import org.rainbow.persistence.dao.IDao;
import org.rainbow.persistence.dao.SearchOptions;
import org.rainbow.service.api.IService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class Service<T> implements IService<T> {
	private IDao<T> dao;

	public IDao<T> getDao() {
		return dao;
	}

	public void setDao(IDao<T> dao) {
		this.dao = dao;
	}

	@Override
	@Transactional(readOnly = false)
	public void create(T entity) throws Exception {
		dao.create(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void update(T entity) throws Exception {
		dao.update(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(T entity) throws Exception {
		dao.delete(entity);
	}

	@Override
	public T findById(Object id) throws Exception {
		return dao.findById(id);
	}

	@Override
	public List<T> findAll() throws Exception {
		return dao.findAll();
	}

	@Override
	public List<T> find(SearchOptions options) throws Exception {
		return dao.find(options);
	}

	@Override
	public long count(SearchOptions options) throws Exception {
		// TODO Auto-generated method stub
		return dao.count(options);
	}
}
