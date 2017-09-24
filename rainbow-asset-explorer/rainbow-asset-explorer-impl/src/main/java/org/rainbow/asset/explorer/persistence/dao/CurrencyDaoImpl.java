package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.Pageable;

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

}
