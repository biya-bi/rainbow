package org.rainbow.security.service.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rainbow.criteria.PathImpl;
import org.rainbow.criteria.PredicateBuilderImpl;
import org.rainbow.criteria.SearchOptionsImpl;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.persistence.dao.ApplicationDao;
import org.rainbow.security.persistence.dao.AuthorityDao;
import org.rainbow.security.persistence.dao.GroupDao;
import org.rainbow.security.persistence.dao.UserDao;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.util.SearchUtil;

public class GroupAuthorityServiceImpl implements GroupAuthorityService {

	private String applicationName;
	private ApplicationDao applicationDao;
	private UserDao userDao;
	private GroupDao groupDao;
	private AuthorityDao authorityDao;

	public GroupAuthorityServiceImpl() {
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public ApplicationDao getApplicationDao() {
		return applicationDao;
	}

	public void setApplicationDao(ApplicationDao applicationDao) {
		this.applicationDao = applicationDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public GroupDao getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public AuthorityDao getAuthorityDao() {
		return authorityDao;
	}

	public void setAuthorityDao(AuthorityDao authorityDao) {
		this.authorityDao = authorityDao;
	}

	@Override
	public void grantAuthoritiesToGroups(List<String> authorityNames, List<String> groupNames)
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException {

		List<Authority> authorities = SearchUtil.getAuthorities(authorityNames, this.getApplicationName(),
				this.getAuthorityDao(), this.getApplicationDao());
		List<Group> groups = SearchUtil.getGroups(groupNames, this.getApplicationName(), this.getGroupDao(),
				this.getApplicationDao());

		List<Authority> authoritiesToBeModified = new ArrayList<>();
		List<Group> groupsToBeModified = new ArrayList<>();

		for (Authority authority : authorities) {

			List<Group> authorityGroups = authority.getGroups();
			if (authorityGroups == null) {
				authorityGroups = new ArrayList<>();
				authority.setGroups(authorityGroups);
			}

			for (Group group : groups) {
				List<Authority> groupAuthorities = group.getAuthorities();
				if (groupAuthorities == null) {
					groupAuthorities = new ArrayList<>();
					group.setAuthorities(groupAuthorities);
				}
				if (groupAuthorities.contains(authority)) {
					throw new AuthorityAlreadyGrantedToGroupException(authority.getName(), group.getName());
				}
				groupAuthorities.add(authority);

				if (!authorityGroups.contains(group)) {
					authorityGroups.add(group);
				}

				groupsToBeModified.add(group);
			}

			authoritiesToBeModified.add(authority);
		}

		updateAuthorities(authoritiesToBeModified);
		updateGroups(groupsToBeModified);

	}

	@Override
	public void revokeAuthoritiesFromGroups(List<String> authorityNames, List<String> groupNames)
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException {

		List<Authority> authorities = SearchUtil.getAuthorities(authorityNames, this.getApplicationName(),
				this.getAuthorityDao(), this.getApplicationDao());
		List<Group> groups = SearchUtil.getGroups(groupNames, this.getApplicationName(), this.getGroupDao(),
				this.getApplicationDao());

		List<Authority> authoritiesToBeModified = new ArrayList<>();
		List<Group> groupsToBeModified = new ArrayList<>();

		for (Authority authority : authorities) {

			List<Group> authorityGroups = authority.getGroups();
			if (authorityGroups == null) {
				authorityGroups = new ArrayList<>();
				authority.setGroups(authorityGroups);
			}

			for (Group group : groups) {
				List<Authority> groupAuthorities = group.getAuthorities();
				if (groupAuthorities == null) {
					groupAuthorities = new ArrayList<>();
					group.setAuthorities(groupAuthorities);
				}
				if (!groupAuthorities.contains(authority)) {
					throw new AuthorityNotGrantedToGroupException(authority.getName(), group.getName());
				}
				groupAuthorities.remove(authority);

				if (authorityGroups.contains(group)) {
					authorityGroups.remove(group);
				}

				groupsToBeModified.add(group);
			}

			authoritiesToBeModified.add(authority);
		}

		updateAuthorities(authoritiesToBeModified);
		updateGroups(groupsToBeModified);

	}

	@Override
	public boolean isInGroups(String userName, List<String> groupNames) {
		if (userName == null)
			throw new IllegalArgumentException("The userName argument cannot be null.");

		if (groupNames == null || groupNames.isEmpty())
			throw new IllegalArgumentException("The groupNames argument can be null nor empty.");

		if (!SearchUtil.userExists(userName, this.getApplicationName(), this.getUserDao(), this.getApplicationDao())) {
			throw new UserNotFoundException(userName);
		}

		if (!SearchUtil.applicationExists(this.getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		List<Group> groups;
		try {
			PredicateBuilderImpl builder = new PredicateBuilderImpl();

			groups = this.getGroupDao().find(
					new SearchOptionsImpl(builder.and(builder.equal(new PathImpl("application.name"), applicationName),
							builder.equal(new PathImpl("users.userName"), userName),
							builder.in(new PathImpl("name"), groupNames))));
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		long loadedGroupCount = groups.stream().map(x -> x.getName().toUpperCase()).distinct().count();
		long providedGroupCount = groupNames.stream().map(x -> x.toUpperCase()).distinct().count();

		return loadedGroupCount > 0 && loadedGroupCount >= providedGroupCount;
	}

	@Override
	public boolean isInGroup(String userName, String groupName) {
		return this.isInGroups(userName, Arrays.asList(groupName));
	}

	private void updateAuthorities(List<Authority> authorities) {
		try {
			this.getAuthorityDao().update(authorities);
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	private void updateGroups(List<Group> groups) {
		try {
			this.getGroupDao().update(groups);
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}
}
