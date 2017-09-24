package org.rainbow.security.service.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserNotFoundException;

public class SearchUtil {

	public static boolean applicationExists(String applicationName,
			Dao<Application, Long, SearchOptions> applicationDao) {
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (applicationDao == null)
			throw new IllegalArgumentException("The applicationDao argument cannot be null.");

		try {
			return applicationDao.count(constructApplicationSearchOptions(applicationName)) > 0;
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	public static Application getApplication(String applicationName,
			Dao<Application, Long, SearchOptions> applicationDao) {
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

	public static boolean userExists(String userName, String applicationName, Dao<User, Long, SearchOptions> userDao,
			Dao<Application, Long, SearchOptions> applicationDao)
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

		try {
			return userDao.count(constructUserSearchOptions(userName, applicationName)) > 0;
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	public static User getUser(String userName, String applicationName, Dao<User, Long, SearchOptions> userDao,
			Dao<Application, Long, SearchOptions> applicationDao)
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

	public static List<User> getUsers(List<String> userNames, String applicationName,
			Dao<User, Long, SearchOptions> userDao, Dao<Application, Long, SearchOptions> applicationDao)
			throws ApplicationNotFoundException, UserNotFoundException {

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

		SearchOptions options = new SearchOptions();
		options.setFilters(Arrays.asList(
				new SingleValuedFilter<String>("application.name", RelationalOperator.EQUAL, applicationName),
				new ListValuedFilter<String>("userName", RelationalOperator.IN, userNames)));

		List<User> users;
		try {
			users = userDao.find(options);
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

	public static List<Group> getGroups(List<String> groupNames, String applicationName,
			Dao<Group, Long, SearchOptions> groupDao, Dao<Application, Long, SearchOptions> applicationDao)
			throws ApplicationNotFoundException, GroupNotFoundException {

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

		SearchOptions options = new SearchOptions();
		options.setFilters(Arrays.asList(
				new SingleValuedFilter<String>("application.name", RelationalOperator.EQUAL, applicationName),
				new ListValuedFilter<String>("name", RelationalOperator.IN, groupNames)));

		List<Group> groups;
		try {
			groups = groupDao.find(options);
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

	private static SearchOptions constructApplicationSearchOptions(String applicationName) {
		SearchOptions options = new SearchOptions();
		options.setFilters(
				Arrays.asList(new SingleValuedFilter<String>("name", RelationalOperator.EQUAL, applicationName)));
		return options;
	}

	private static SearchOptions constructUserSearchOptions(String userName, String applicationName) {
		SearchOptions options = new SearchOptions();
		options.setFilters(Arrays.asList(new SingleValuedFilter<String>("userName", RelationalOperator.EQUAL, userName),
				new SingleValuedFilter<String>("application.name", RelationalOperator.EQUAL, applicationName)));
		return options;
	}

	public static List<Authority> getAuthorities(List<String> authorityNames, String applicationName,
			Dao<Authority, Long, SearchOptions> authorityDao, Dao<Application, Long, SearchOptions> applicationDao)
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

		SearchOptions options = new SearchOptions();
		options.setFilters(Arrays.asList(
				new SingleValuedFilter<String>("application.name", RelationalOperator.EQUAL, applicationName),
				new ListValuedFilter<String>("name", RelationalOperator.IN, authorityNames)));

		List<Authority> authorities;
		try {
			authorities = authorityDao.find(options);
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
