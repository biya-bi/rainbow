package org.rainbow.service.services;

import java.io.Serializable;
import java.util.List;

/**
 * Interface representing the service layer. It focuses on tasks, such as
 * coordinating business logic, demarcating transactions, applying security
 * restrictions, doing validations, and so on. The service layer individually
 * requests data access operations from the data access layer, or DAO layer for
 * short; and data access-specific implementation details are completely hidden
 * behind its interface. Service objects depend on one or several different data
 * access objects through their interfaces, and they make use of those DAO
 * objects while executing their business tasks at hand.
 * 
 * @author Biya-Bi
 *
 * @param <TEntity>
 *            the type of the entity serviced by this layer.
 * @param <TKey>
 *            the type of the primary key of the entity serviced by this layer.
 *            This parameter is required because the
 *            {@link org.rainbow.service.services.Service#findById(TKey) findById} method
 *            to get an entity by ID.
 * @param <TSearchOptions>
 *            the type of the search criteria used to find and count entities.
 *            This parameter is used in the
 *            {@link org.rainbow.service.services.Service#find(TSearchOptions) find} and
 *            {@link org.rainbow.service.services.Service#count(TSearchOptions) count}
 *            methods.
 */
public interface Service<TEntity extends Object, TKey extends Serializable, TSearchOptions extends Object> {
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
	 * @return a {@code T} whose ID is supplied as argument.
	 * @throws Exception
	 *             if an error occurs while searching the entity with the given
	 *             ID
	 *
	 */
	TEntity findById(TKey id) throws Exception;

	/**
	 * Get all entities
	 * 
	 * @throws Exception
	 *             if an error occurs while searching all the entities
	 *
	 */
	List<TEntity> findAll() throws Exception;

	/**
	 * Get entities that satisfy the specified {@code TSearchOptions}.
	 * 
	 * @param options
	 *            the search options used to search for the entities
	 * @return the entities that satisfy the specified search options
	 * @throws Exception
	 *             if an error occurs while searching the entities
	 */
	List<TEntity> find(TSearchOptions options) throws Exception;

	/**
	 * Get count of entities that satisfy the specified {@code TSearchOptions}.
	 * 
	 * @param options
	 *            the search options used to count the entities
	 * @return the number of entities that satisfy the specified search options
	 * @throws Exception
	 *             if an error occurs while counting the entities
	 */
	long count(TSearchOptions options) throws Exception;

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