package org.rainbow.security.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.persistence.DaoImpl;
import org.rainbow.persistence.Pageable;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.utilities.PersistenceSettings;
import org.rainbow.utilities.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class GroupDaoImpl extends DaoImpl<Group, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public GroupDaoImpl() {
		super(Group.class);
	}

	private void fixAssociations(Group group) {
		group.setApplication(
				EntityManagerUtil.find(this.getEntityManager(), Application.class, group.getApplication()));
	}

	@Override
	protected void onCreate(Group group) throws Exception {
		super.onCreate(group);
		fixAssociations(group);
	}

	@Override
	protected void onUpdate(Group group) throws Exception {
		super.onUpdate(group);
		fixAssociations(group);
	}
}
