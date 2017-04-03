package org.rainbow.security.core.service;

import java.util.List;

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.persistence.dao.UserGroupDao;
import org.rainbow.security.core.persistence.exceptions.ApplicationNotFoundException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserNotInGroupException;
import org.rainbow.security.core.utilities.TransactionSettings;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = true)
public class UserGroupServiceImpl implements UserGroupService {
	private final UserGroupDao dao;

	public UserGroupServiceImpl(UserGroupDao dao) {
		if (dao == null)
			throw new IllegalArgumentException("The dao argument cannot be null.");
		this.dao = dao;
	}

	@Override
	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = false)
	public void addUsersToGroups(List<Long> userIds, List<Long> groupIds, Long applicationId)
			throws UserNotFoundException, GroupNotFoundException, UserAlreadyInGroupException,
			NonexistentEntityException {
		this.dao.addUsersToGroups(userIds, groupIds, applicationId);
	}

	@Override
	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = false)
	public void removeUsersFromGroups(List<Long> userIds, List<Long> groupIds, Long applicationId)
			throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, NonexistentEntityException {
		this.dao.removeUsersFromGroups(userIds, groupIds, applicationId);
	}

	@Override
	public List<String> getGroups(String userName, String applicationName)
			throws UserNotFoundException, ApplicationNotFoundException {
		return this.dao.getGroups(userName, applicationName);
	}

	@Override
	public List<String> getAuthorities(String userName, String applicationName)
			throws UserNotFoundException, ApplicationNotFoundException {
		return this.dao.getAuthorities(userName, applicationName);
	}

}
