package org.rainbow.service.api;

import java.util.List;

import org.rainbow.persistence.dao.SearchOptions;

public interface IService<T> {
	/**
	 * Add an entity
	 *
	 * @param entity
	 *            the entity to be added.
	 * @throws Exception 
	 * 
	 */
	public void create(T entity) throws Exception;

	/**
	 * Update an entity
	 *
	 * @param entity
	 *            the entity to be updated.
	 * @throws Exception 
	 * 
	 */
	public void update(T entity) throws Exception;

	/**
	 * Delete an entity
	 *
	 * @param entity
	 *            the entity to be deleted.
	 * @throws Exception 
	 * 
	 */
	public void delete(T entity) throws Exception;

	/**
	 * Get an entity by id
	 *
	 * @param id
	 *            the ID of the entity to be searched.
	 * @return a {@code T} whose ID is supplied as argument.
	 * @throws Exception 
	 *
	 */
	public T findById(Object id) throws Exception;

	/**
	 * Get all entities
	 * @throws Exception 
	 *
	 */
	public List<T> findAll() throws Exception;

	public List<T> find(SearchOptions options) throws Exception;

	public long count(SearchOptions options) throws Exception;
}