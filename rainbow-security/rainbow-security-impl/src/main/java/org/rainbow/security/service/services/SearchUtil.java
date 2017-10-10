package org.rainbow.security.service.services;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.rainbow.criteria.PathImpl;
import org.rainbow.criteria.PredicateBuilderImpl;
import org.rainbow.criteria.SearchOptionsImpl;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.persistence.dao.ApplicationDao;
import org.rainbow.security.persistence.dao.AuthorityDao;
import org.rainbow.security.persistence.dao.GroupDao;
import org.rainbow.security.persistence.dao.UserDao;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserNotFoundException;

public class SearchUtil {

	public static boolean applicationExists(String applicationName, ApplicationDao applicationDao) {
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		try {
			return applicationDao.count(constructApplicationSearchOptions(applicationName).getPredicate()) > 0;
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	public static Application getApplication(String applicationName, ApplicationDao applicationDao) {
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		List<Application> applications;

		try {
			applications = applicationDao.find(constructApplicationSearchOptions(applicationName));

		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		if (applications == null || applications.isEmpty())
			throw new ApplicationNotFoundException(applicationName);

		return applications.get(0);
	}

	public static boolean userExists(String userName, String applicationName, UserDao userDao,
			ApplicationDao applicationDao) throws ApplicationNotFoundException, UserNotFoundException {
		if (userName == null)
			throw new IllegalArgumentException("The userName argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (userDao == null)
			throw new IllegalArgumentException("The userDao argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		if (!applicationExists(applicationName, applicationDao)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		try {
			return userDao.count(constructUserSearchOptions(userName, applicationName).getPredicate()) > 0;
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	public static User getUser(String userName, String applicationName, UserDao userDao, ApplicationDao applicationDao)
			throws ApplicationNotFoundException, UserNotFoundException {
		if (userName == null)
			throw new IllegalArgumentException("The userName argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (userDao == null)
			throw new IllegalArgumentException("The userDao argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		if (!applicationExists(applicationName, applicationDao)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		List<User> users = null;

		try {
			users = userDao.find(constructUserSearchOptions(userName, applicationName));
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		if (users == null || users.isEmpty()) {
			throw new UserNotFoundException(userName);
		}

		return users.get(0);
	}

	public static List<User> getUsers(List<String> userNames, String applicationName, UserDao userDao,
			ApplicationDao applicationDao) throws ApplicationNotFoundException, UserNotFoundException {

		if (userNames == null)
			throw new IllegalArgumentException("The userNames argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (userDao == null)
			throw new IllegalArgumentException("The userDao argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		if (!applicationExists(applicationName, applicationDao)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		List<User> users;
		try {
			PredicateBuilderImpl builder = new PredicateBuilderImpl();
			users = userDao.find(
					new SearchOptionsImpl(builder.and(builder.equal(new PathImpl("application.name"), applicationName),
							builder.in(new PathImpl("userName"), userNames))));
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		List<String> foundUserNames = users.stream().map(u -> u.getUserName().toUpperCase())
				.collect(Collectors.toList());

		Collections.sort(foundUserNames);

		String notFoundUserName = userNames.stream()
				.filter(x -> x != null && Collections.binarySearch(foundUserNames, x.toUpperCase()) < 0).findFirst()
				.orElse(null);

		if (notFoundUserName != null) {
			throw new UserNotFoundException(notFoundUserName);
		}

		return users;
	}

	public static List<Group> getGroups(List<String> groupNames, String applicationName, GroupDao groupDao,
			ApplicationDao applicationDao) throws ApplicationNotFoundException, GroupNotFoundException {

		if (groupNames == null)
			throw new IllegalArgumentException("The groupNames argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (groupDao == null)
			throw new IllegalArgumentException("The groupDao argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		if (!applicationExists(applicationName, applicationDao)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		List<Group> groups;
		try {
			PredicateBuilderImpl builder = new PredicateBuilderImpl();

			groups = groupDao.find(
					new SearchOptionsImpl(builder.and(builder.equal(new PathImpl("application.name"), applicationName),
							builder.in(new PathImpl("name"), groupNames))));
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		List<String> foundGroupNames = groups.stream().map(u -> u.getName().toUpperCase()).collect(Collectors.toList());

		Collections.sort(foundGroupNames);

		String notFoundGroupName = groupNames.stream()
				.filter(x -> x != null && Collections.binarySearch(foundGroupNames, x.toUpperCase()) < 0).findFirst()
				.orElse(null);

		if (notFoundGroupName != null) {
			throw new GroupNotFoundException(notFoundGroupName);
		}

		return groups;
	}

	private static SearchOptionsImpl constructApplicationSearchOptions(String applicationName) {
		SearchOptionsImpl searchOptions = new SearchOptionsImpl();
		PredicateBuilderImpl builder = new PredicateBuilderImpl();
		searchOptions.setPredicate(builder.equal(new PathImpl("name"), applicationName));
		return searchOptions;
	}

	private static SearchOptionsImpl constructUserSearchOptions(String userName, String applicationName) {
		SearchOptionsImpl searchOptions = new SearchOptionsImpl();
		PredicateBuilderImpl builder = new PredicateBuilderImpl();
		searchOptions.setPredicate(builder.and(builder.equal(new PathImpl("userName"), userName),
				builder.equal(new PathImpl("application.name"), applicationName)));

		return searchOptions;
	}

	public static List<Authority> getAuthorities(List<String> authorityNames, String applicationName,
			AuthorityDao authorityDao, ApplicationDao applicationDao)
			throws ApplicationNotFoundException, AuthorityNotFoundException {

		if (authorityNames == null)
			throw new IllegalArgumentException("The authorityNames argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (authorityDao == null)
			throw new IllegalArgumentException("The authorityDao argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		if (!applicationExists(applicationName, applicationDao)) {
			throw new ApplicationNotFoundException(applicationName);
		}

		List<Authority> authorities;
		try {
			PredicateBuilderImpl builder = new PredicateBuilderImpl();

			authorities = authorityDao.find(
					new SearchOptionsImpl(builder.and(builder.equal(new PathImpl("application.name"), applicationName),
							builder.in(new PathImpl("name"), authorityNames))));
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		List<String> foundAuthorityNames = authorities.stream().map(u -> u.getName().toUpperCase())
				.collect(Collectors.toList());

		Collections.sort(foundAuthorityNames);

		String notFoundAuthorityName = authorityNames.stream()
				.filter(x -> x != null && Collections.binarySearch(foundAuthorityNames, x.toUpperCase()) < 0)
				.findFirst().orElse(null);

		if (notFoundAuthorityName != null) {
			throw new AuthorityNotFoundException(notFoundAuthorityName);
		}

		return authorities;
	}

}
