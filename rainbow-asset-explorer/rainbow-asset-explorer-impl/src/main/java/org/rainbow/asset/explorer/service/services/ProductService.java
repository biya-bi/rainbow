package org.rainbow.asset.explorer.service.services;

import java.util.List;

import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.Service;

public interface ProductService extends Service<Product, Long, SearchOptions> {
	/**
	 * Find products by product numbers.
	 * 
	 * @param productNumbers the list of product numbers for which to find the products.
	 * @return the list of products corresponding to the provided list of product numbers.
	 */
	List<Product> find(List<String> productNumbers);

}