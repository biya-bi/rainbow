package org.rainbow.security.core.persistence.dao;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.UpdateOperation;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.PasswordPolicy;
import org.rainbow.security.core.entities.Authority;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.entities.TokenPolicy;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.DuplicateApplicationNameException;
import org.rainbow.security.core.utilities.EntityManagerHelper;
import org.rainbow.security.core.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class ApplicationDao extends DaoImpl<Application, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	private final Dao<User, Long, SearchOptions> userDao;
	private final Dao<Authority, Long, SearchOptions> authorityDao;
	private final Dao<Group, Long, SearchOptions> groupDao;

	public ApplicationDao(Dao<User, Long, SearchOptions> userDao, Dao<Authority, Long, SearchOptions> authorityDao,
			Dao<Group, Long, SearchOptions> groupDao) {
		super(Application.class);
		if (userDao == null)
			throw new IllegalArgumentException("The userDao argument cannot be null.");
		if (authorityDao == null)
			throw new IllegalArgumentException("The authorityDao argument cannot be null.");
		if (groupDao == null)
			throw new IllegalArgumentException("The groupDao argument cannot be null.");
		this.userDao = userDao;
		this.authorityDao = authorityDao;
		this.groupDao = groupDao;
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Application application, UpdateOperation operation) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		switch (operation) {
		case CREATE:
			if (helper.isDuplicate(Application.class, application.getName(), "name", "id", null)) {
				throw new DuplicateApplicationNameException(application.getName());
			}
			break;
		case UPDATE:
			if (helper.isDuplicate(Application.class, application.getName(), "name", "id", application.getId())) {
				throw new DuplicateApplicationNameException(application.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void create(Collection<Application> applications) throws Exception {
		for (Application application : applications) {
			fixDependenciesOnCreate(application);

			List<User> users = application.getUsers();
			List<Group> groups = application.getRoles();
			List<Authority> authorities = application.getPermissions();

			application.setUsers(null);
			application.setRoles(null);
			application.setPermissions(null);

			this.create(application);
			// em.flush();

			if (users != null) {
				for (User user : users) {
					user.setGroups(null);

					userDao.create(user);
				}
			}

			if (authorities != null) {
				for (Authority authority : authorities) {
					authority.setGroups(null);

					authorityDao.create(authority);

				}
			}

			if (groups != null) {
				for (Group group : groups) {
					groupDao.create(group);
				}
			}
		}
		// em.clear();
	}

	@Override
	public void create(Application application) throws Exception {
		fixDependenciesOnCreate(application);

		super.create(application);
	}

	private void fixDependenciesOnCreate(Application application) {
		PasswordPolicy passwordPolicy = application.getPasswordPolicy();
		if (passwordPolicy != null && !application.equals(passwordPolicy.getApplication())) {
			passwordPolicy.setApplication(application);
		}
		TokenPolicy tokenPolicy = application.getTokenPolicy();
		if (tokenPolicy != null && !application.equals(tokenPolicy.getApplication())) {
			tokenPolicy.setApplication(application);
		}
	}

}
