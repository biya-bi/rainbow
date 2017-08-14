/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.ShipMethod;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateShipMethodNameException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

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

	@Override
	protected void validate(ShipMethod shipMethod, UpdateOperation operation) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = shipMethod.getId();
			if (helper.isDuplicate(ShipMethod.class, shipMethod.getName(), "name", "id", id)) {
				throw new DuplicateShipMethodNameException(shipMethod.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
