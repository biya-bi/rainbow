/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Authority;
import org.rainbow.security.core.persistence.exceptions.DuplicateAuthorityNameException;
import org.rainbow.security.core.utilities.EntityManagerHelper;
import org.rainbow.security.core.utilities.PersistenceSettings;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class AuthorityDao extends DaoImpl<Authority, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AuthorityDao() {
		super(Authority.class);
	}

	@Override
	protected void validate(Authority authority, UpdateOperation operation) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Application persistentApplication;
		switch (operation) {
		case CREATE:
			persistentApplication = helper.getReference(Application.class, authority.getApplication().getId());
			authority.setApplication(persistentApplication);
			if (helper.isDuplicate(Authority.class, authority.getName(), "name", "id", null)) {
				throw new DuplicateAuthorityNameException(authority.getName(), persistentApplication.getName());
			}
			break;
		case UPDATE:
			persistentApplication = helper.getReference(Application.class, authority.getApplication().getId());
			authority.setApplication(persistentApplication);
			if (helper.isDuplicate(Authority.class, authority.getName(), "name", "id", authority.getId())) {
				throw new DuplicateAuthorityNameException(authority.getName(), persistentApplication.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
