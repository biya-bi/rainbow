package org.rainbow.security.service.services;

import java.util.List;

import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;

public interface GroupAuthorityService {

	void grantAuthoritiesToGroups(List<String> authorityNames, List<String> groupNames)
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException;

	void revokeAuthoritiesFromGroups(List<String> authorityNames, List<String> groupNames)
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException;

	boolean isInGroups(String userName, List<String> groupNames);

	boolean isInGroup(String userName, String groupName);
}
