package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.Pageable;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ShipMethodDaoImpl extends TrackableDaoImpl<ShipMethod, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ShipMethodDaoImpl() {
		super(ShipMethod.class);
	}
}
