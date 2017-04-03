package org.rainbow.core.service;

import java.io.Serializable;
import java.util.List;

public interface Service<TEntity, TKey extends Serializable, TSearchOptions> {
	/**
	 * Add an entity
	 *
	 * @param entity
	 *            the entity to be added.
	 * @throws Exception
	 * 
	 */
	public void create(TEntity entity) throws Exception;

	/**
	 * Update an entity
	 *
	 * @param entity
	 *            the entity to be updated.
	 * @throws Exception
	 * 
	 */
	public void update(TEntity entity) throws Exception;

	/**
	 * Delete an entity
	 *
	 * @param entity
	 *            the entity to be deleted.
	 * @throws Exception
	 * 
	 */
	public void delete(TEntity entity) throws Exception;

	/**
	 * Get an entity by id
	 *
	 * @param id
	 *            the ID of the entity to be searched.
	 * @return a {@code T} whose ID is supplied as argument.
	 * @throws Exception
	 *
	 */
	public TEntity findById(TKey id) throws Exception;

	/**
	 * Get all entities
	 * 
	 * @throws Exception
	 *
	 */
	public List<TEntity> findAll() throws Exception;

	public List<TEntity> find(TSearchOptions options) throws Exception;

	public long count(TSearchOptions options) throws Exception;
}