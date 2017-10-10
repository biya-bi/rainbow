package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.dao.Pageable;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ProductInventoryDaoImpl extends TrackableDaoImpl<ProductInventory> implements ProductInventoryDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProductInventoryDaoImpl() {
		super(ProductInventory.class);
	}

}