package org.rainbow.service;

import java.util.List;

import org.rainbow.persistence.IDao;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.IService;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class Service<T> implements IService<T> {
	private IDao<T> dao;

	public Service(IDao<T> dao) {
		if (dao == null)
			throw new IllegalArgumentException("The dao argument cannot be null.");
		this.dao = dao;
	}

	public IDao<T> getDao() {
		return dao;
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
		return dao.count(options);
	}
}
