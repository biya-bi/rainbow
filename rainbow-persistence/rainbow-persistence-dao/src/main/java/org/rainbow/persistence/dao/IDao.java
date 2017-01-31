package org.rainbow.persistence.dao;

import java.util.Collection;
import java.util.List;

public interface IDao<T extends Object> {
	/**
	 * Add an entity
	 *
	 * @param entity
	 *            the entity to be added.
	 * 
	 */
	void create(T entity) throws Exception;

	/**
	 * Update an entity
	 *
	 * @param entity
	 *            the entity to be updated.
	 * 
	 */
	void update(T entity) throws Exception;

	/**
	 * Delete an entity
	 *
	 * @param entity
	 *            the entity to be deleted.
	 * 
	 */
	void delete(T entity) throws Exception;

	/**
	 * Get an entity by id
	 *
	 * @param id
	 *            the ID of the entity to be searched.
	 * @return a {@code T} whose ID is supplied as argument.
	 *
	 */
	T findById(Object id) throws Exception;

	/**
	 * Get all entities
	 *
	 */
	List<T> findAll() throws Exception;

	void create(Collection<T> entities) throws Exception;

	void update(Collection<T> entities) throws Exception;

	void delete(Collection<T> entities) throws Exception;

	List<T> find(SearchOptions options) throws Exception;

	long count(SearchOptions options) throws Exception;

}
