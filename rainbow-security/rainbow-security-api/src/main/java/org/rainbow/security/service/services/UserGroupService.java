package org.rainbow.security.service.services;

import java.util.List;

import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.UserNotInGroupException;

public interface UserGroupService {

	void addUsersToGroups(List<String> userNames, List<String> groupNames)
			throws UserNotFoundException, GroupNotFoundException, UserAlreadyInGroupException;

	void removeUsersFromGroups(List<String> userNames, List<String> groupNames)
			throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException;

	List<String> getGroups(String userName) throws UserNotFoundException;

}
