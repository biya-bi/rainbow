package org.rainbow.security.service.services;

import java.util.List;

import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToUserException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToUserException;
import org.rainbow.security.service.exceptions.UserNotFoundException;

public interface UserAuthorityService {

	void grantAuthoritiesToUsers(List<String> authorityNames, List<String> userNames)
			throws AuthorityNotFoundException, UserNotFoundException, AuthorityAlreadyGrantedToUserException;

	void revokeAuthoritiesFromUsers(List<String> authorityNames, List<String> userNames)
			throws AuthorityNotFoundException, UserNotFoundException, AuthorityNotGrantedToUserException;

	List<String> getAuthorities(String userName) throws UserNotFoundException;

	boolean hasAllAuthorities(String userName, List<String> authorityNames);

	boolean hasAuthority(String userName, String authorityName);
	
	boolean hasAnyAuthority(String userName, List<String> authorityNames);
}
