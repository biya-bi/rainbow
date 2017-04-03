package org.rainbow.security.core.service;

import java.util.List;

import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.persistence.exceptions.ApplicationNotFoundException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserNotInGroupException;

public interface UserGroupService {

	void addUsersToGroups(List<Long> userIds, List<Long> groupIds, Long applicationId) throws UserNotFoundException,
			GroupNotFoundException, UserAlreadyInGroupException, NonexistentEntityException;

	void removeUsersFromGroups(List<Long> userIds, List<Long> groupIds, Long applicationId)
			throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, NonexistentEntityException;

	List<String> getGroups(String userName, String applicationName)
			throws UserNotFoundException, ApplicationNotFoundException;

	List<String> getAuthorities(String userName, String applicationName)
			throws UserNotFoundException, ApplicationNotFoundException;

}
