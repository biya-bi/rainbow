package org.rainbow.security.persistence.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.LockoutPolicy;
import org.rainbow.security.orm.entities.LoginPolicy;
import org.rainbow.security.orm.entities.PasswordPolicy;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class ApplicationDaoImpl extends DaoImpl<Application> implements ApplicationDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public ApplicationDaoImpl() {
		super(Application.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void onCreate(Application application) throws Exception {
		super.onCreate(application);
		fixAssociations(application);
	}

	@Override
	protected void onUpdate(Application application) throws Exception {
		super.onUpdate(application);
		fixAssociations(application);
	}

	private void fixAssociations(Application application) {
		final PasswordPolicy passwordPolicy = application.getPasswordPolicy();
		if (passwordPolicy != null && !application.equals(passwordPolicy.getApplication())) {
			passwordPolicy.setApplication(application);
		}
		final LockoutPolicy lockoutPolicy = application.getLockoutPolicy();
		if (lockoutPolicy != null && !application.equals(lockoutPolicy.getApplication())) {
			lockoutPolicy.setApplication(application);
		}
		final LoginPolicy loginPolicy = application.getLoginPolicy();
		if (loginPolicy != null && !application.equals(loginPolicy.getApplication())) {
			loginPolicy.setApplication(application);
		}
		final List<User> users = application.getUsers();
		if (users != null) {
			for (User user : users) {
				if (!application.equals(user.getApplication())) {
					user.setApplication(application);
				}
			}
		}
		final List<Group> groups = application.getGroups();
		if (groups != null) {
			for (Group group : groups) {
				if (!application.equals(group.getApplication())) {
					group.setApplication(application);
				}
			}
		}
		final List<Authority> authorities = application.getAuthorities();
		if (authorities != null) {
			for (Authority authority : authorities) {
				if (!application.equals(authority.getApplication())) {
					authority.setApplication(application);
				}
			}
		}
	}

}
