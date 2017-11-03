package org.rainbow.security.service.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.rainbow.criteria.PathImpl;
import org.rainbow.criteria.PredicateBuilderImpl;
import org.rainbow.criteria.SearchOptionsImpl;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.persistence.dao.ApplicationDao;
import org.rainbow.security.persistence.dao.AuthorityDao;
import org.rainbow.security.persistence.dao.GroupDao;
import org.rainbow.security.persistence.dao.UserDao;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.UserNotInGroupException;
import org.rainbow.security.util.SearchUtil;

public class UserGroupServiceImpl implements UserGroupService {

	private String applicationName;
	private ApplicationDao applicationDao;
	private UserDao userDao;
	private GroupDao groupDao;
	private AuthorityDao authorityDao;

	public UserGroupServiceImpl() {
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

			PredicateBuilderImpl builder = new PredicateBuilderImpl();
			groups = this.getGroupDao().find(
					new SearchOptionsImpl(builder.and(builder.equal(new PathImpl("application.name"), applicationName),
							builder.equal(new PathImpl("users.userName"), userName))));
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
