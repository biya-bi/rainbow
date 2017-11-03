package org.rainbow.security.service.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.rainbow.criteria.PathImpl;
import org.rainbow.criteria.PredicateBuilderImpl;
import org.rainbow.criteria.SearchOptionsImpl;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.persistence.dao.ApplicationDao;
import org.rainbow.security.persistence.dao.AuthorityDao;
import org.rainbow.security.persistence.dao.UserDao;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToUserException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToUserException;
import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.util.SearchUtil;

public class UserAuthorityServiceImpl implements UserAuthorityService {

	private String applicationName;
	private ApplicationDao applicationDao;
	private UserDao userDao;
	private AuthorityDao authorityDao;

	public UserAuthorityServiceImpl() {
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

	public AuthorityDao getAuthorityDao() {
		return authorityDao;
	}

	public void setAuthorityDao(AuthorityDao authorityDao) {
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

			PredicateBuilderImpl builder = new PredicateBuilderImpl();

			authoritiesGrantedToUserGroups = this.getAuthorityDao()
					.find(new SearchOptionsImpl(
							builder.and(builder.equal(new PathImpl("application.name"), this.getApplicationName()),
									builder.equal(new PathImpl("groups.users.userName"), userName))));

			authoritiesGrantedToUser = this.getAuthorityDao()
					.find(new SearchOptionsImpl(
							builder.and(builder.equal(new PathImpl("application.name"), this.getApplicationName()),
									builder.equal(new PathImpl("users.userName"), userName))));

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
			PredicateBuilderImpl builder = new PredicateBuilderImpl();
			authoritiesGrantedToUserGroups = this.getAuthorityDao()
					.find(new SearchOptionsImpl(
							builder.and(builder.equal(new PathImpl("application.name"), this.getApplicationName()),
									builder.equal(new PathImpl("groups.users.userName"), userName))));

			authoritiesGrantedToUser = this.getAuthorityDao()
					.find(new SearchOptionsImpl(
							builder.and(builder.equal(new PathImpl("application.name"), this.getApplicationName()),
									builder.equal(new PathImpl("users.userName"), userName))));

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
