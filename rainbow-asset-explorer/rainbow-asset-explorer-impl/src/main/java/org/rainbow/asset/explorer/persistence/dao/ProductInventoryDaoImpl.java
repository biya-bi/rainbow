package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.util.PersistenceSettings;
import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.util.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ProductInventoryDaoImpl extends DaoImpl<ProductInventory> implements ProductInventoryDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProductInventoryDaoImpl() {
		super(ProductInventory.class);
	}

	@Override
	protected void onCreate(ProductInventory productInventory) throws Exception {
		fixAssociations(productInventory);
		super.onCreate(productInventory);
	}

	@Override
	protected void onUpdate(ProductInventory productInventory) throws Exception {
		fixAssociations(productInventory);
		super.onUpdate(productInventory);
	}

	private void fixAssociations(ProductInventory productInventory) {
		if (productInventory.getId() != null && productInventory.getId().getProductId() != null) {
			productInventory.setProduct(EntityManagerUtil.findById(this.getEntityManager(), Product.class,
					productInventory.getId().getProductId()));
		}
		if (productInventory.getLocation() != null && productInventory.getId().getLocationId() != null) {
			productInventory.setLocation(EntityManagerUtil.findById(this.getEntityManager(), Location.class,
					productInventory.getId().getLocationId()));
		}
	}
}
