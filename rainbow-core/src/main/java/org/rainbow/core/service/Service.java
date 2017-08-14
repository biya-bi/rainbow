package org.rainbow.core.service;

import java.io.Serializable;
import java.util.Collection;
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
	void create(TEntity entity) throws Exception;

	/**
	 * Update an entity
	 *
	 * @param entity
	 *            the entity to be updated.
	 * @throws Exception
	 * 
	 */
	void update(TEntity entity) throws Exception;

	/**
	 * Delete an entity
	 *
	 * @param entity
	 *            the entity to be deleted.
	 * @throws Exception
	 * 
	 */
	void delete(TEntity entity) throws Exception;

	/**
	 * Get an entity by id
	 *
	 * @param id
	 *            the ID of the entity to be searched.
	 * @return a {@code T} whose ID is supplied as argument.
	 * @throws Exception
	 *
	 */
	TEntity findById(TKey id) throws Exception;

	/**
	 * Get all entities
	 * 
	 * @throws Exception
	 *
	 */
	List<TEntity> findAll() throws Exception;

	List<TEntity> find(TSearchOptions options) throws Exception;

	long count(TSearchOptions options) throws Exception;

	void create(Collection<TEntity> entities) throws Exception;

	void update(Collection<TEntity> entities) throws Exception;

	void delete(Collection<TEntity> entities) throws Exception;
}