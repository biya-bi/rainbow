/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Manufacturer;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateProductNameException;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateProductNumberException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.ListValuedFilter;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ProductDaoImpl extends TrackableDaoImpl<Product, Long> implements ProductDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProductDaoImpl() {
		super(Product.class);
	}

	@Override
	protected void validate(Product product, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = product.getId();
			if (helper.isDuplicate(Product.class, product.getNumber(), "number", "id", id)) {
				throw new DuplicateProductNumberException(product.getNumber());
			}
			if (helper.isDuplicate(Product.class, product.getName(), "name", "id", id)) {
				throw new DuplicateProductNameException(product.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	private void setAssociations(Product product) throws Exception {
		if (product.getManufacturer() != null) {
			product.setManufacturer(new EntityManagerHelper(em).find(Manufacturer.class, product.getManufacturer()));
		}
	}

	@Override
	public void create(Product entity) throws Exception {
		setAssociations(entity);
		super.create(entity);
	}

	@Override
	public void update(Product entity) throws Exception {
		setAssociations(entity);
		super.update(entity);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rainbow.asset.explorer.core.persistence.dao.ProductDao#find(java.util
	 * .List)
	 */
	@Override
	public List<Product> find(List<String> productNumbers) {
		ListValuedFilter<String> filter = new ListValuedFilter<>("number");
		filter.setOperator(RelationalOperator.IN);
		filter.setList(productNumbers);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);

		SearchOptions options = new SearchOptions();
		options.setFilters(filters);

		return find(options);
	}
}
