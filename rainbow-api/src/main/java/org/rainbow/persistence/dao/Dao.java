package org.rainbow.persistence.dao;

import java.util.List;

import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.SearchOptions;

/**
 * Interface representing the data access layer.
 * 
 * @author Biya-Bi
 *
 * @param <TEntity>
 *            the type of the entity handled by this layer.
 */
public interface Dao<TEntity extends Object> {
	/**
	 * Create an entity
	 *
	 * @param entity
	 *            the entity to be added.
	 * @throws Exception
	 *             if an error occurs while creating the entity
	 * 
	 */
	void create(TEntity entity) throws Exception;

	/**
	 * Update an entity
	 *
	 * @param entity
	 *            the entity to be updated.
	 * @throws Exception
	 *             if an error occurs while updating the entity
	 * 
	 */
	void update(TEntity entity) throws Exception;

	/**
	 * Delete an entity
	 *
	 * @param entity
	 *            the entity to be deleted.
	 * @throws Exception
	 *             if an error occurs while deleting the entity
	 * 
	 */
	void delete(TEntity entity) throws Exception;

	/**
	 * Get an entity by id
	 *
	 * @param id
	 *            the ID of the entity to be searched.
	 * @return a {@code TEntity} whose ID is supplied as argument.
	 * @throws Exception
	 *             if an error occurs while searching the entity with the given
	 *             ID
	 *
	 */
	TEntity findById(Object id) throws Exception;

	/**
	 * Get all entities
	 * 
	 * @throws Exception
	 *             if an error occurs while searching all the entities
	 *
	 */
	List<TEntity> findAll() throws Exception;

	/**
	 * Get entities that satisfy the specified {@link SearchOptions}.
	 * 
	 * @param options
	 *            the search options used to search for the entities
	 * @return the entities that satisfy the specified search options
	 * @throws Exception
	 *             if an error occurs while searching the entities
	 */
	List<TEntity> find(SearchOptions options) throws Exception;

	/**
	 * Get count of entities that satisfy the specified {@link Predicate}.
	 * 
	 * @param predicate
	 *            the predicate used to count the entities
	 * @return the number of entities that satisfy the specified search options
	 * @throws Exception
	 *             if an error occurs while counting the entities
	 */
	long count(Predicate predicate) throws Exception;

	/**
	 * Create entities
	 *
	 * @param entities
	 *            the entities to be added.
	 * @throws Exception
	 *             if an error occurs while creating the entities
	 * 
	 */
	void create(List<TEntity> entities) throws Exception;

	/**
	 * Update entities
	 *
	 * @param entities
	 *            the entities to be updated.
	 * @throws Exception
	 *             if an error occurs while updating the entities
	 * 
	 */
	void update(List<TEntity> entities) throws Exception;

	/**
	 * Delete entities
	 *
	 * @param entities
	 *            the entities to be deleted.
	 * @throws Exception
	 *             if an error occurs while deleting the entities
	 * 
	 */
	void delete(List<TEntity> entities) throws Exception;
}
