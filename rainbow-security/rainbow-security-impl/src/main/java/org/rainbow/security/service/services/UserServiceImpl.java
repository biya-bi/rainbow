package org.rainbow.security.service.services;

import java.util.HashMap;
import java.util.Map;

import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.dao.Dao;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.DuplicateUserException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.utilities.PasswordUtil;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class UserServiceImpl extends ServiceImpl<User, Long, SearchOptions> {

	private Dao<Application, Long, SearchOptions> applicationDao;

	public UserServiceImpl() {
	}

	public Dao<Application, Long, SearchOptions> getApplicationDao() {
		return applicationDao;
	}

	public void setApplicationDao(Dao<Application, Long, SearchOptions> applicationDao) {
		this.applicationDao = applicationDao;
	}

	@Override
	protected void validate(User user, UpdateOperation operation) throws Exception {
		super.validate(user, operation);

		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> filters = new HashMap<>();
			filters.put("userName", user.getUserName());
			filters.put("application.id", user.getApplication().getId());
			if (DaoUtil.isDuplicate(this.getDao(), filters, user.getId(), operation)) {
				throw new DuplicateUserException(user.getUserName());
			}
			if (operation == UpdateOperation.CREATE) {
				final Membership membership = user.getMembership();
				if (membership != null) {
					final Application application = this.getApplicationDao().findById(user.getApplication().getId());
					if (application != null) {
						if (!PasswordUtil.isValidPassword(membership.getPassword(), application.getPasswordPolicy())) {
							throw new InvalidPasswordException();
						}
					}
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
