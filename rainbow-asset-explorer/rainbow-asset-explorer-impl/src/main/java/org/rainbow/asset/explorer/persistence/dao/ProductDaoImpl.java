package org.rainbow.asset.explorer.persistence.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.Manufacturer;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.Filter;
import org.rainbow.persistence.ListValuedFilter;
import org.rainbow.persistence.Pageable;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.utilities.EntityManagerUtil;

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
	protected void onCreate(Product entity) throws Exception {
		super.onCreate(entity);
		fixAssociations(entity);
	}

	@Override
	protected void onUpdate(Product entity) throws Exception {
		super.onUpdate(entity);
		fixAssociations(entity);
	}

	private void fixAssociations(Product product) throws Exception {
		if (product.getManufacturer() != null) {
			product.setManufacturer(
					EntityManagerUtil.find(this.getEntityManager(), Manufacturer.class, product.getManufacturer()));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rainbow.asset.explorer.persistence.dao.ProductDao#find(java.util
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
