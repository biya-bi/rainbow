/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Currency;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateCurrencyNameException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class CurrencyDaoImpl extends TrackableDaoImpl<Currency, Integer> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public CurrencyDaoImpl() {
		super(Currency.class);
	}

	@Override
	protected void validate(Currency currency, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Integer id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = currency.getId();
			if (helper.isDuplicate(Currency.class, currency.getName(), "name", "id", id)) {
				throw new DuplicateCurrencyNameException(currency.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
