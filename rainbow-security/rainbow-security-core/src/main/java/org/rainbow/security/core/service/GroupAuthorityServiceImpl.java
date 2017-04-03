package org.rainbow.security.core.service;

import java.util.List;

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.persistence.dao.GroupAuthorityDao;
import org.rainbow.security.core.persistence.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotFoundException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;
import org.rainbow.security.core.utilities.TransactionSettings;
import org.springframework.transaction.annotation.Transactional;

@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = true)
public class GroupAuthorityServiceImpl implements GroupAuthorityService {

	private final GroupAuthorityDao dao;

	public GroupAuthorityServiceImpl(GroupAuthorityDao dao) {
		if (dao == null)
			throw new IllegalArgumentException("The dao argument cannot be null.");
		this.dao = dao;
	}

	@Override
	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = false)
	public void grantAuthoritiesToGroups(List<Long> authorityIds, List<Long> groupIds, Long applicationId)
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException,
			NonexistentEntityException {

		this.dao.grantAuthoritiesToGroups(authorityIds, groupIds, applicationId);

	}

	@Override
	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, readOnly = false)
	public void revokeAuthoritiesFromGroups(List<Long> authorityIds, List<Long> groupIds, Long applicationId)
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException,
			NonexistentEntityException {

		this.dao.revokeAuthoritiesFromGroups(authorityIds, groupIds, applicationId);

	}

	@Override
	public boolean hasAuthorities(String userName, String applicationName, String... authorityNames) {
		return this.dao.hasAuthorities(userName, applicationName, authorityNames);
	}

	@Override
	public boolean hasAuthority(String userName, String applicationName, String authorityName) {
		return this.dao.hasAuthority(userName, applicationName, authorityName);
	}

	@Override
	public boolean isInGroups(String userName, String applicationName, String... groupNames) {
		return this.dao.isInGroups(userName, applicationName, groupNames);
	}

	@Override
	public boolean isInGroup(String userName, String applicationName, String groupName) {
		return this.dao.isInGroup(userName, applicationName, groupName);
	}

	public GroupAuthorityDao getDao() {
		return dao;
	}

}
