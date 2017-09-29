package org.rainbow.security.service.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.persistence.dao.Dao;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToUserException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToUserException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.services.UserAuthorityService;

public class UserAuthorityServiceImpl implements UserAuthorityService {

	private String applicationName;
	private Dao<Application, Long, SearchOptions> applicationDao;
	private Dao<User, Long, SearchOptions> userDao;
	private Dao<Authority, Long, SearchOptions> authorityDao;

	public UserAuthorityServiceImpl() {
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

	public Dao<Authority, Long, SearchOptions> getAuthorityDao() {
		return authorityDao;
	}

	public void setAuthorityDao(Dao<Authority, Long, SearchOptions> authorityDao) {
		this.authorityDao = authorityDao;
	}

	private void updateAuthorities(List<Authority> authorities) {
		try {
			this.getAuthorityDao().update(authorities);
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	private void updateUsers(List<User> users) {
		try {
			this.getUserDao().update(users);
		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}
	}

	@Override
	public void grantAuthoritiesToUsers(List<String> authorityNames, List<String> userNames)
			throws AuthorityNotFoundException, UserNotFoundException, AuthorityAlreadyGrantedToUserException {

		List<Authority> authorities = SearchUtil.getAuthorities(authorityNames, this.getApplicationName(),
				this.getAuthorityDao(), this.getApplicationDao());
		List<User> users = SearchUtil.getUsers(userNames, this.getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		List<Authority> authoritiesToBeModified = new ArrayList<>();
		List<User> usersToBeModified = new ArrayList<>();

		for (Authority authority : authorities) {

			List<User> authorityUsers = authority.getUsers();
			if (authorityUsers == null) {
				authorityUsers = new ArrayList<>();
				authority.setUsers(authorityUsers);
			}

			for (User user : users) {
				List<Authority> groupAuthorities = user.getAuthorities();
				if (groupAuthorities == null) {
					groupAuthorities = new ArrayList<>();
					user.setAuthorities(groupAuthorities);
				}
				if (groupAuthorities.contains(authority)) {
					throw new AuthorityAlreadyGrantedToUserException(authority.getName(), user.getUserName());
				}
				groupAuthorities.add(authority);

				if (!authorityUsers.contains(user)) {
					authorityUsers.add(user);
				}

				usersToBeModified.add(user);
			}

			authoritiesToBeModified.add(authority);
		}

		updateAuthorities(authoritiesToBeModified);
		updateUsers(usersToBeModified);

	}

	@Override
	public void revokeAuthoritiesFromUsers(List<String> authorityNames, List<String> userNames)
			throws AuthorityNotFoundException, UserNotFoundException, AuthorityNotGrantedToUserException {

		List<Authority> authorities = SearchUtil.getAuthorities(authorityNames, this.getApplicationName(),
				this.getAuthorityDao(), this.getApplicationDao());
		List<User> users = SearchUtil.getUsers(userNames, this.getApplicationName(), this.getUserDao(),
				this.getApplicationDao());

		List<Authority> authoritiesToBeModified = new ArrayList<>();
		List<User> usersToBeModified = new ArrayList<>();

		for (Authority authority : authorities) {

			List<User> authorityUsers = authority.getUsers();
			if (authorityUsers == null) {
				authorityUsers = new ArrayList<>();
				authority.setUsers(authorityUsers);
			}

			for (User group : users) {
				List<Authority> groupAuthorities = group.getAuthorities();
				if (groupAuthorities == null) {
					groupAuthorities = new ArrayList<>();
					group.setAuthorities(groupAuthorities);
				}
				if (!groupAuthorities.contains(authority)) {
					throw new AuthorityNotGrantedToUserException(authority.getName(), group.getUserName());
				}
				groupAuthorities.remove(authority);

				if (authorityUsers.contains(group)) {
					authorityUsers.remove(group);
				}

				usersToBeModified.add(group);
			}

			authoritiesToBeModified.add(authority);
		}

		updateAuthorities(authoritiesToBeModified);
		updateUsers(usersToBeModified);

	}

	@Override
	public List<String> getAuthorities(String userName) throws UserNotFoundException {
		if (userName == null) {
			throw new IllegalArgumentException("The userName argument cannot be null.");
		}

		if (!SearchUtil.applicationExists(this.getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		if (!SearchUtil.userExists(userName, this.getApplicationName(), this.getUserDao(), this.getApplicationDao())) {
			throw new UserNotFoundException(userName);
		}

		List<Authority> authoritiesGrantedToUserGroups;
		List<Authority> authoritiesGrantedToUser;

		try {
			SearchOptions options = new SearchOptions();

			final SingleValuedFilter<String> applicationNameFilter = new SingleValuedFilter<String>("application.name",
					RelationalOperator.EQUAL, this.getApplicationName());
			final SingleValuedFilter<String> groupUsersFilter = new SingleValuedFilter<String>("groups.users.userName",
					RelationalOperator.EQUAL, userName);

			options.setFilters(Arrays.asList(applicationNameFilter, groupUsersFilter));

			authoritiesGrantedToUserGroups = this.getAuthorityDao().find(options);

			final SingleValuedFilter<String> userNameFilter = new SingleValuedFilter<String>("users.userName",
					RelationalOperator.EQUAL, userName);

			options.setFilters(Arrays.asList(applicationNameFilter, userNameFilter));

			authoritiesGrantedToUser = this.getAuthorityDao().find(options);

		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		List<String> authorities = new ArrayList<>();

		authorities.addAll(authoritiesGrantedToUser.stream().map(x -> x.getName()).collect(Collectors.toList()));
		authorities.addAll(authoritiesGrantedToUserGroups.stream().map(x -> x.getName()).collect(Collectors.toList()));

		return authorities.stream().distinct().collect(Collectors.toList());

	}

	@Override
	public boolean hasAllAuthorities(String userName, List<String> authorityNames) {
		List<String> loadedAuthorities = getAuthorities(userName, authorityNames);
		List<String> providedAuthorities = authorityNames.stream().distinct().collect(Collectors.toList());
		return !loadedAuthorities.isEmpty() && loadedAuthorities.size() >= providedAuthorities.size();
	}

	private List<String> getAuthorities(String userName, List<String> authorityNames) {
		if (userName == null)
			throw new IllegalArgumentException("The userName argument cannot be null.");

		if (authorityNames == null || authorityNames.isEmpty())
			throw new IllegalArgumentException("The authorityNames argument can be null nor empty.");

		if (!SearchUtil.userExists(userName, this.getApplicationName(), this.getUserDao(), this.getApplicationDao())) {
			throw new UserNotFoundException(userName);
		}

		if (!SearchUtil.applicationExists(this.getApplicationName(), this.getApplicationDao())) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}

		List<Authority> authoritiesGrantedToUserGroups;
		List<Authority> authoritiesGrantedToUser;

		try {
			SearchOptions options = new SearchOptions();

			final SingleValuedFilter<String> applicationNameFilter = new SingleValuedFilter<String>("application.name",
					RelationalOperator.EQUAL, this.getApplicationName());
			final SingleValuedFilter<String> groupUsersFilter = new SingleValuedFilter<String>("groups.users.userName",
					RelationalOperator.EQUAL, userName);

			options.setFilters(Arrays.asList(applicationNameFilter, groupUsersFilter));

			authoritiesGrantedToUserGroups = this.getAuthorityDao().find(options);

			final SingleValuedFilter<String> userNameFilter = new SingleValuedFilter<String>("users.userName",
					RelationalOperator.EQUAL, userName);

			options.setFilters(Arrays.asList(applicationNameFilter, userNameFilter));

			authoritiesGrantedToUser = this.getAuthorityDao().find(options);

		} catch (Exception e) {
			throw new RainbowSecurityServiceException(e);
		}

		List<String> authorities = new ArrayList<>();

		authorities.addAll(authoritiesGrantedToUser.stream().map(x -> x.getName()).collect(Collectors.toList()));
		authorities.addAll(authoritiesGrantedToUserGroups.stream().map(x -> x.getName()).collect(Collectors.toList()));

		return authorities.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public boolean hasAuthority(String userName, String authorityName) {
		return this.hasAllAuthorities(userName, Arrays.asList(authorityName));
	}

	@Override
	public boolean hasAnyAuthority(String userName, List<String> authorityNames) {
		return !this.getAuthorities(userName, authorityNames).isEmpty();
	}

}
