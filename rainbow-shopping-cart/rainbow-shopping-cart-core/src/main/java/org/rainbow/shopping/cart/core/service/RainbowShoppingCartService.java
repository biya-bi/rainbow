package org.rainbow.shopping.cart.core.service;

import java.io.Serializable;
import java.util.List;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.service.Service;
import org.rainbow.shopping.cart.core.utilities.TransactionSettings;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = true)
public class RainbowShoppingCartService<TEntity, TKey extends Serializable, TSearchOptions>
		implements Service<TEntity, TKey, TSearchOptions> {
	private Dao<TEntity, TKey, TSearchOptions> dao;

	public RainbowShoppingCartService(Dao<TEntity, TKey, TSearchOptions> dao) {
		if (dao == null)
			throw new IllegalArgumentException("The dao argument cannot be null.");
		this.dao = dao;
	}

	public Dao<TEntity, TKey, TSearchOptions> getDao() {
		return dao;
	}

	@Override
	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = false)
	public void create(TEntity entity) throws Exception {
		dao.create(entity);
	}

	@Override
	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = false)
	public void update(TEntity entity) throws Exception {
		dao.update(entity);
	}

	@Override
	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = false)
	public void delete(TEntity entity) throws Exception {
		dao.delete(entity);
	}

	@Override
	public TEntity findById(TKey id) throws Exception {
		return dao.findById(id);
	}

	@Override
	public List<TEntity> findAll() throws Exception {
		return dao.findAll();
	}

	@Override
	public List<TEntity> find(TSearchOptions options) throws Exception {
		return dao.find(options);
	}

	@Override
	public long count(TSearchOptions options) throws Exception {
		return dao.count(options);
	}
}
