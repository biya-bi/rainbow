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
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.persistence.exceptions.DuplicateGroupException;
import org.rainbow.security.core.utilities.EntityManagerHelper;
import org.rainbow.security.core.utilities.PersistenceSettings;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class GroupDao extends DaoImpl<Group, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public GroupDao() {
		super(Group.class);
	}

	@Override
	protected void validate(Group group, UpdateOperation operation) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Application persistentApplication;
		switch (operation) {
		case CREATE:
			persistentApplication = helper.getReference(Application.class, group.getApplication().getId());
			group.setApplication(persistentApplication);
			if (helper.isDuplicate(Group.class, group.getName(), "name", "id", null)) {
				throw new DuplicateGroupException(group.getName(), persistentApplication.getName());
			}
			break;
		case UPDATE:
			persistentApplication = helper.getReference(Application.class, group.getApplication().getId());
			group.setApplication(persistentApplication);
			if (helper.isDuplicate(Group.class, group.getName(), "name", "id", group.getId())) {
				throw new DuplicateGroupException(group.getName(), persistentApplication.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
