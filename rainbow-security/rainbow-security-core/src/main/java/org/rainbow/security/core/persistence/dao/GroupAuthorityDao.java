package org.rainbow.security.core.persistence.dao;

import java.util.List;

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.persistence.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotFoundException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;

public interface GroupAuthorityDao {
	
	void grantAuthoritiesToGroups(List<Long> authorityIds, List<Long> groupIds, Long applicationId)
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException,
			NonexistentEntityException;

	void revokeAuthoritiesFromGroups(List<Long> authorityIds, List<Long> groupIds, Long applicationId)
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException,
			NonexistentEntityException;

	boolean hasAuthorities(String userName, String applicationName, String... authorityNames);

	boolean hasAuthority(String userName, String applicationName, String authorityName);

	boolean isInGroups(String userName, String applicationName, String... groupNames);

	boolean isInGroup(String userName, String applicationName, String groupName);

}
