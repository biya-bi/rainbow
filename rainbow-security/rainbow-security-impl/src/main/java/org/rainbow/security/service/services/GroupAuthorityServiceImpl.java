package org.rainbow.security.service.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.rainbow.persistence.Dao;
import org.rainbow.persistence.ListValuedFilter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.services.GroupAuthorityService;

public class GroupAuthorityServiceImpl implements GroupAuthorityService {

	private String applicationName;
	private Dao<Application, Long, SearchOptions> applicationDao;
	private Dao<User, Long, SearchOptions> userDao;
	private Dao<Group, Long, SearchOptions> groupDao;
	private Dao<Authority, Long, SearchOptions> authorityDao;

	public GroupAuthorityServiceImpl() {
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public Dao<Application, Long, SearchOptions> getApplicationDao() {
		return applicationDao;
	}

	public void setApplicationDao(Dao<Application, Long, SearchOptions> applicationDao) {
		this.applicationDao = applicationDao;
	}

	public Dao<User, Long, SearchOptions> getUserDao() {
		return userDao;
	}

	public void setUserDao(Dao<User, Long, SearchOptions> userDao) {
		this.userDao = userDao;
	}

	public Dao<Group, Long, SearchOptions> getGroupDao() {
		return groupDao;
	}

	public void setGroupDao(Dao<Group, Long, SearchOptions> groupDao) {
		this.groupDao = groupDao;
	}

	public Dao<Authority, Long, SearchOptions> getAuthorityDao() {
		return authorityDao;
	}

	public void setAuthorityDao(Dao<Authority, Long, SearchOptions> authorityDao) {
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

		SearchOptions options = new SearchOptions();
		options.setFilters(Arrays.asList(
				new SingleValuedFilter<String>("application.name", RelationalOperator.EQUAL, this.getApplicationName()),
				new SingleValuedFilter<String>("users.userName", RelationalOperator.EQUAL, userName),
				new ListValuedFilter<String>("name", RelationalOperator.IN, groupNames)));

		List<Group> groups;
		try {
			groups = this.getGroupDao().find(options);
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
