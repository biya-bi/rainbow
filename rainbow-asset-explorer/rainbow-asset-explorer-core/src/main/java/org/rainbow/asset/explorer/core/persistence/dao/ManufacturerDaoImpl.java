/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Manufacturer;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateManufacturerNameException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class ManufacturerDaoImpl extends TrackableDaoImpl<Manufacturer, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ManufacturerDaoImpl() {
		super(Manufacturer.class);
	}

	@Override
	protected void validate(Manufacturer manufacturer, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = manufacturer.getId();
			if (helper.isDuplicate(Manufacturer.class, manufacturer.getName(), "name", "id", id)) {
				throw new DuplicateManufacturerNameException(manufacturer.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
