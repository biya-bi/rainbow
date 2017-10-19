package org.rainbow.asset.explorer.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.Manufacturer;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.util.PersistenceSettings;
import org.rainbow.criteria.PathImpl;
import org.rainbow.criteria.PredicateBuilderImpl;
import org.rainbow.criteria.SearchOptionsImpl;
import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.util.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ProductDaoImpl extends DaoImpl<Product> implements ProductDao {

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
		SearchOptionsImpl searchOptions = new SearchOptionsImpl();
		PredicateBuilderImpl builder = new PredicateBuilderImpl();
		searchOptions.setPredicate(builder.in(new PathImpl("number"), productNumbers));

		return find(searchOptions);
	}
}
