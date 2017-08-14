/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Locale;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateLocaleLcidException;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateLocaleNameException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class LocaleDaoImpl extends TrackableDaoImpl<Locale, Integer> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public LocaleDaoImpl() {
		super(Locale.class);
	}

	@Override
	protected void validate(Locale locale, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Integer id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = locale.getId();
			if (helper.isDuplicate(Locale.class, locale.getName(), "name", "id", id)) {
				throw new DuplicateLocaleNameException(locale.getName());
			}
			if (locale.getLcid() != null && !locale.getLcid().isEmpty()) {
				if (helper.isDuplicate(Locale.class, locale.getLcid(), "lcid", "id", id)) {
					throw new DuplicateLocaleLcidException(locale.getLcid());
				}
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
