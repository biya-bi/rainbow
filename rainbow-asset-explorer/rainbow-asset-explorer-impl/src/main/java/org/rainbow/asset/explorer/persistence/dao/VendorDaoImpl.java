package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.util.PersistenceSettings;
import org.rainbow.persistence.dao.Pageable;

@Pageable(attributeName = "id")
public class VendorDaoImpl extends BusinessEntityDao<Vendor> implements VendorDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public VendorDaoImpl() {
		super(Vendor.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}
}
