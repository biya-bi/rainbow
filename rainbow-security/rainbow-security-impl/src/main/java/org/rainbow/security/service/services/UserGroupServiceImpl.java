package org.rainbow.security.service.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.rainbow.persistence.Dao;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.UserNotInGroupException;
import org.rainbow.security.service.services.UserGroupService;

public class UserGroupServiceImpl implements UserGroupService {

	private String applicationName;
	private Dao<Application, Long, SearchOptions> applicationDao;
	private Dao<User, Long, SearchOptions> userDao;
	private Dao<Group, Long, SearchOptions> groupDao;
	private Dao<Authority, Long, SearchOptions> authorityDao;

	public UserGroupServiceImpl() {
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
	public void addUsersToGroups(List<String> userNames, List<String> groupNames)
			throws UserNotFoundException, GroupNotFoundException, UserAlreadyInGroupException {

		List<User> users = SearchUtil.getUsers(userNames, this.getApplicationName(), this.getUserDao(),
				this.getApplicationDao());
		List<Group> groups = SearchUtil.getGroups(groupNames, this.getApplicationName(), this.getGroupDao(),
				this.getApplicationDao());

		List<User> usersToBeModified = new ArrayList<>();
		List<Group> groupsToBeModified = new ArrayList<>();

		for (User user : users) {

			List<Group> userGroups = user.getGroups();
			if (userGroups == null) {
				userGroups = new ArrayList<>();
				user.setGroups(userGroups);
			}

			for (Group group : groups) {
				List<User> groupUsers = group.getUsers();
				if (groupUsers == null) {
					groupUsers = new ArrayList<>();
					group.setUsers(groupUsers);
				}
				if (groupUsers.contains(user)) {
					throw new UserAlreadyInGroupException(user.getUserName(), group.getName());
				}
				groupUsers.add(user);

				if (!userGroups.contains(group)) {
					userGroups.add(group);
				}

				groupsToBeModified.add(group);
			}

			usersToBeModified.add(user);
		}

		updateUsers(usersToBeModified);
		updateGroups(groupsToBeModified);
	}

	@Override
	public void removeUsersFromGroups(List<String> userNames, List<String> groupNames)
			throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException {

		List<User> users = SearchUtil.getUsers(userNames, this.getApplicationName(), this.getUserDao(),
				this.getApplicationDao());
		List<Group> groups = SearchUtil.getGroups(groupNames, this.getApplicationName(), this.getGroupDao(),
				this.getApplicationDao());

		List<User> usersToBeModified = new ArrayList<>();
		List<Group> groupsToBeModified = new ArrayList<>();

		for (User user : users) {

			List<Group> userGroups = user.getGroups();
			if (userGroups == null) {
				userGroups = new ArrayList<>();
				user.setGroups(userGroups);
			}

			for (Group group : groups) {
				List<User> groupUsers = group.getUsers();
				if (groupUsers == null) {
					groupUsers = new ArrayList<>();
					group.setUsers(groupUsers);
				}
				if (!groupUsers.contains(user)) {
					throw new UserNotInGroupException(user.getUserName(), group.getName());
				}
				groupUsers.remove(user);

				if (userGroups.contains(group)) {
					userGroups.remove(group);
				}

				groupsToBeModified.add(group);
			}

			usersToBeModified.add(user);
		}

		updateUsers(usersToBeModified);
		updateGroups(groupsToBeModified);

	}

	@Override
	public List<String> getGroups(String userName) throws UserNotFoundException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}

		if (!SearchUtil.applicationExists(this.getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		if (!SearchUtil.userExists(userName, this.getApplicationName(), this.getUserDao(), this.getApplicationDao())) {
			throw new UserNotFoundException(userName);
		}

		List<Group> groups;

		try {
			SearchOptions options = new SearchOptions();
			options.setFilters(Arrays.asList(
					new SingleValuedFilter<String>("application.name", RelationalOperator.EQUAL,
							this.getApplicationName()),
					new SingleValuedFilter<String>("users.userName", RelationalOperator.EQUAL, userName)));

			groups = this.getGroupDao().find(options);
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		return groups.stream().map(x -> x.getName()).distinct().collect(Collectors.toList());
	}

	private void updateUsers(List<User> users) {
		try {
			this.getUserDao().update(users);
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
