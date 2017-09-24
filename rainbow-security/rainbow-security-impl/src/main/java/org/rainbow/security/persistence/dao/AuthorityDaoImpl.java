package org.rainbow.security.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.persistence.DaoImpl;
import org.rainbow.persistence.Pageable;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.utilities.PersistenceSettings;
import org.rainbow.utilities.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class AuthorityDaoImpl extends DaoImpl<Authority, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AuthorityDaoImpl() {
		super(Authority.class);
	}

	private void fixAssociations(Authority authority) {
		authority.setApplication(
				EntityManagerUtil.find(this.getEntityManager(), Application.class, authority.getApplication()));
	}

	@Override
	protected void onCreate(Authority authority) throws Exception {
		super.onCreate(authority);
		fixAssociations(authority);
	}

	@Override
	protected void onUpdate(Authority authority) throws Exception {
		super.onUpdate(authority);
		fixAssociations(authority);
	}
}
