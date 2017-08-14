/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.SchedulerException;
import org.rainbow.asset.explorer.core.entities.AlertType;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.entities.ProductInventory;
import org.rainbow.asset.explorer.core.entities.ProductInventoryId;
import org.rainbow.asset.explorer.core.persistence.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.core.persistence.exceptions.RainbowAssetExplorerException;
import org.rainbow.asset.explorer.core.scheduling.AlertScheduler;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
public class InventoryManagerImpl implements InventoryManager {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("alertScheduler")
	private AlertScheduler alertScheduler;

	private static final String PRODUCT_INVENTORY_ID_ATTR = "id";
	private static final String PRODUCT_INVENTORY_LOCATION_ID_ATTR = "locationId";
	private static final String PRODUCT_INVENOTORY_PRODUCT_ID_ATTR = "productId";
	private static final String PRODUCT_INVENTORY_QUANTITY_ATTR = "quantity";
	private static final String PRODUCT_ID_ATTR = "id";
	private static final String PRODUCT_NUMBER_ATTR = "number";
	private static final String PRODUCT_NAME_ATTR = "name";
	private static final String PRODUCT_NUMBER_PARAM = "productNumber";
	private static final String PRODUCT_NAME_PARAM = "productName";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.asset.explorer.core.service.InventoryManager#
	 * getProductInventories(java.lang.Long)
	 */
	@Override
	public List<ProductInventory> getProductInventories(Long locationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductInventory> cq = cb.createQuery(ProductInventory.class);
		Root<ProductInventory> rt = cq.from(ProductInventory.class);
		cq.select(rt);
		Predicate p = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR), locationId);
		cq.where(p);
		TypedQuery<ProductInventory> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.service.InventoryManager#load(java.lang.
	 * Long, java.util.List)
	 */
	@Override
	public List<ProductInventory> load(Long locationId, List<Long> productIds) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<ProductInventory> cq = cb.createQuery(ProductInventory.class);
		Root<ProductInventory> rt = cq.from(ProductInventory.class);
		cq.select(rt);
		Predicate p1 = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR), locationId);
		Predicate p2 = rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENOTORY_PRODUCT_ID_ATTR).in(productIds);
		cq.where(cb.and(p1, p2));
		TypedQuery<ProductInventory> tq = em.createQuery(cq);
		return tq.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.service.InventoryManager#load(java.lang.
	 * Long)
	 */
	@Override
	public Map<Product, Short> load(Long locationId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<ProductInventory> rt = cq.from(ProductInventory.class);
		Root<Product> rt1 = cq.from(Product.class);
		cq.select(cb.tuple(rt1, rt.get(PRODUCT_INVENTORY_QUANTITY_ATTR)));
		Predicate p1 = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR), locationId);
		Predicate p2 = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENOTORY_PRODUCT_ID_ATTR),
				rt1.get(PRODUCT_ID_ATTR));
		cq.where(cb.and(p1, p2));
		TypedQuery<Tuple> tq = em.createQuery(cq);
		List<Tuple> tuples = tq.getResultList();
		Map<Product, Short> result = new HashMap<>();
		for (Tuple tuple : tuples) {
			result.put((Product) tuple.get(0), (short) tuple.get(1));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.service.InventoryManager#load(java.lang.
	 * Long, java.lang.String, java.lang.String, java.lang.Integer,
	 * java.lang.Integer)
	 */
	@Override
	public Map<Product, Short> load(Long locationId, String productNumberFilter, String productNameFilter,
			Integer pageIndex, Integer pageSize) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<ProductInventory> rt = cq.from(ProductInventory.class);
		Root<Product> rt1 = cq.from(Product.class);
		cq.select(cb.tuple(rt1, rt.get(PRODUCT_INVENTORY_QUANTITY_ATTR)));
		Predicate p1 = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR), locationId);
		Predicate p2 = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENOTORY_PRODUCT_ID_ATTR),
				rt1.get(PRODUCT_ID_ATTR));
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(p1);
		predicates.add(p2);
		if (productNumberFilter != null) {
			Predicate p3 = cb.like(cb.upper(rt1.<String>get(PRODUCT_NUMBER_ATTR)),
					cb.parameter(String.class, PRODUCT_NUMBER_PARAM));
			predicates.add(p3);
		}
		if (productNameFilter != null) {
			Predicate p4 = cb.like(cb.upper(rt1.<String>get(PRODUCT_NAME_ATTR)),
					cb.parameter(String.class, PRODUCT_NAME_PARAM));
			predicates.add(p4);
		}
		Predicate predicate = null;
		for (Predicate p : predicates) {
			if (predicate == null) {
				predicate = p;
			} else {
				predicate = cb.and(predicate, p);
			}
		}
		cq.where(predicate);

		TypedQuery<Tuple> tq = em.createQuery(cq);

		if (productNumberFilter != null && !productNumberFilter.isEmpty()) {
			tq.setParameter(PRODUCT_NUMBER_PARAM, "%" + productNumberFilter.trim().toUpperCase() + "%");
		}

		if (productNameFilter != null && !productNameFilter.isEmpty()) {
			tq.setParameter(PRODUCT_NAME_PARAM, "%" + productNameFilter.trim().toUpperCase() + "%");
		}

		if (pageIndex != null && pageSize != null) {
			tq.setFirstResult(pageIndex * pageSize);
		}
		if (pageSize != null) {
			tq.setMaxResults(pageSize);
		}

		List<Tuple> tuples = tq.getResultList();
		Map<Product, Short> result = new HashMap<>();
		for (Tuple tuple : tuples) {
			result.put((Product) tuple.get(0), (short) tuple.get(1));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.service.InventoryManager#count(java.lang.
	 * Long, java.lang.String, java.lang.String)
	 */
	@Override
	public long count(Long locationId, String productNumberFilter, String productNameFilter) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<ProductInventory> rt = cq.from(ProductInventory.class);
		Root<Product> rt1 = cq.from(Product.class);

		cq.select(cb.count(rt));

		Predicate p1 = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENTORY_LOCATION_ID_ATTR), locationId);
		Predicate p2 = cb.equal(rt.get(PRODUCT_INVENTORY_ID_ATTR).get(PRODUCT_INVENOTORY_PRODUCT_ID_ATTR),
				rt1.get(PRODUCT_ID_ATTR));
		List<Predicate> predicates = new ArrayList<>();
		predicates.add(p1);
		predicates.add(p2);
		if (productNumberFilter != null) {
			Predicate p3 = cb.like(cb.upper(rt1.<String>get(PRODUCT_NUMBER_ATTR)),
					cb.parameter(String.class, PRODUCT_NUMBER_PARAM));
			predicates.add(p3);
		}
		if (productNameFilter != null) {
			Predicate p4 = cb.like(cb.upper(rt1.<String>get(PRODUCT_NAME_ATTR)),
					cb.parameter(String.class, PRODUCT_NAME_PARAM));
			predicates.add(p4);
		}
		Predicate predicate = null;
		for (Predicate p : predicates) {
			if (predicate == null) {
				predicate = p;
			} else {
				predicate = cb.and(predicate, p);
			}
		}
		cq.where(predicate);

		TypedQuery<Long> tq = em.createQuery(cq);

		if (productNumberFilter != null && !productNumberFilter.isEmpty()) {
			tq.setParameter(PRODUCT_NUMBER_PARAM, "%" + productNumberFilter.trim().toUpperCase() + "%");
		}

		if (productNameFilter != null && !productNameFilter.isEmpty()) {
			tq.setParameter(PRODUCT_NAME_PARAM, "%" + productNameFilter.trim().toUpperCase() + "%");
		}

		return (long) tq.getSingleResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.service.InventoryManager#add(java.lang.
	 * Long, java.util.Map)
	 */
	@Override
	public void add(Long locationId, Map<Long, Short> productIdsQuantities) {
		List<Long> productIds = new ArrayList<Long>(productIdsQuantities.keySet());
		List<ProductInventory> productInventories = load(locationId, productIds);
		for (Long productId : productIds) {
			boolean found = false;
			Short addedQuantity = productIdsQuantities.get(productId);
			for (ProductInventory productInventory : productInventories) {
				if (productInventory.getId().getProductId().equals(productId)) {
					productInventory.setQuantity((short) (productInventory.getQuantity() + addedQuantity));
					em.merge(productInventory);
					found = true;
					break;
				}
			}
			if (!found) {
				ProductInventory productInventory = new ProductInventory();
				productInventory.setId(new ProductInventoryId(productId, locationId));
				productInventory.setQuantity(addedQuantity);
				productInventories.add(productInventory);
				em.persist(productInventory);
			}
		}

		try {
			alertScheduler.schedule(AlertType.RECOVERY, locationId, productIds);
		} catch (SchedulerException e) {
			throw new RainbowAssetExplorerException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.service.InventoryManager#substract(java.
	 * lang.Long, java.util.Map)
	 */
	@Override
	public void substract(Long locationId, Map<Long, Short> productIdsQuantities)
			throws InsufficientInventoryException {
		List<Long> productIds = new ArrayList<Long>(productIdsQuantities.keySet());
		List<ProductInventory> productInventories = load(locationId, productIds);
		for (Long productId : productIds) {
			boolean found = false;
			Short substractedQuantity = productIdsQuantities.get(productId);
			for (ProductInventory productInventory : productInventories) {
				if (productInventory.getId().getProductId().equals(productId)) {
					short availableQuantity = productInventory.getQuantity();
					short remainingQuantity = (short) (availableQuantity - substractedQuantity);
					if (remainingQuantity < 0) {
						throw new InsufficientInventoryException(locationId, productId, availableQuantity,
								substractedQuantity);
					}
					productInventory.setQuantity(remainingQuantity);
					if (remainingQuantity >= 0) {
						em.merge(productInventory);
					} else {
						em.remove(productInventory);
					}
					found = true;
					break;
				}
			}
			if (!found) {
				throw new InsufficientInventoryException(locationId, productId, (short) 0, substractedQuantity);
			}
		}
		try {
			alertScheduler.schedule(AlertType.WARNING, locationId, productIds);
		} catch (SchedulerException e) {
			throw new RainbowAssetExplorerException(e);
		}
	}
}
