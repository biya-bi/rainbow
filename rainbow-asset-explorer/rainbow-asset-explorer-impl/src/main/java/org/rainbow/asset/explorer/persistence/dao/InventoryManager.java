package org.rainbow.asset.explorer.persistence.dao;

import java.util.List;
import java.util.Map;

import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.service.exceptions.InsufficientInventoryException;

public interface InventoryManager {

	List<ProductInventory> getProductInventories(Long locationId);

	List<ProductInventory> load(Long locationId, List<Long> productIds);

	Map<Product, Short> load(Long locationId);

	Map<Product, Short> load(Long locationId, String productNumberFilter, String productNameFilter, Integer pageIndex,
			Integer pageSize);

	long count(Long locationId, String productNumberFilter, String productNameFilter);

	void add(Long locationId, Map<Long, Short> productIdsQuantities);

	void substract(Long locationId, Map<Long, Short> productIdsQuantities) throws InsufficientInventoryException;

}