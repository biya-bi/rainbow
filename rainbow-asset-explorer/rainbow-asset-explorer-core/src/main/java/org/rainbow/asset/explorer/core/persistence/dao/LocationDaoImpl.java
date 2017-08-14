/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateLocationNameException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class LocationDaoImpl extends TrackableDaoImpl<Location, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public LocationDaoImpl() {
		super(Location.class);
	}

	@Override
	protected void validate(Location location, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = location.getId();
			if (helper.isDuplicate(Location.class, location.getName(), "name", "id", id)) {
				throw new DuplicateLocationNameException(location.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}