package org.rainbow.asset.explorer.service.services;

import java.util.List;

import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.service.services.Service;

public interface ProductService extends Service<Product> {
	/**
	 * Find products by product numbers.
	 * 
	 * @param productNumbers
	 *            the list of product numbers for which to find the products.
	 * @return the list of products corresponding to the provided list of
	 *         product numbers.
	 */
	List<Product> find(List<String> productNumbers);
}
