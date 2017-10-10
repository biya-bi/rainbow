package org.rainbow.asset.explorer.persistence.dao;

import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.service.exceptions.InsufficientInventoryException;

public interface InventoryManager {

	List<ProductInventory> getProductInventories(Long locationId) throws Exception;

	List<ProductInventory> load(Long locationId, List<Long> productIds) throws Exception;

	Map<Product, Short> load(Long locationId) throws Exception;

	Map<Product, Short> load(Long locationId, String productNumberFilter, String productNameFilter, Integer pageIndex,
			Integer pageSize) throws Exception;

	long count(Long locationId, String productNumberFilter, String productNameFilter);

	void add(Long locationId, Map<Long, Short> productIdsQuantities) throws Exception;

	void substract(Long locationId, Map<Long, Short> productIdsQuantities) throws InsufficientInventoryException, Exception;

}