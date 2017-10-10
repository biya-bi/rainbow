package org.rainbow.security.service.services;

import java.util.HashMap;
import java.util.Map;

import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.persistence.dao.ApplicationDao;
import org.rainbow.security.service.exceptions.DuplicateUserException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.utilities.PasswordUtil;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class UserServiceImpl extends ServiceImpl<User> implements UserService {

	private ApplicationDao applicationDao;

	public UserServiceImpl() {
	}

	public ApplicationDao getApplicationDao() {
		return applicationDao;
	}

	public void setApplicationDao(ApplicationDao applicationDao) {
		this.applicationDao = applicationDao;
	}

	@Override
	protected void validate(User user, UpdateOperation operation) throws Exception {
		super.validate(user, operation);

		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> pathValuePairs = new HashMap<>();
			pathValuePairs.put("userName", user.getUserName());
			pathValuePairs.put("application.id", user.getApplication().getId());
			if (DaoUtil.isDuplicate(this.getDao(), pathValuePairs, user.getId(), operation)) {
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
