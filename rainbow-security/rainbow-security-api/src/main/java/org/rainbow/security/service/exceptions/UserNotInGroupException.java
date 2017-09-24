package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class UserNotInGroupException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2594518955710154590L;
	private final String userName;
	private final String groupName;

	public UserNotInGroupException(String userName, String groupName) {
		this(userName, groupName, String.format("The user '%s' is not in the group '%s'.", userName, groupName));
	}

	public UserNotInGroupException(String userName, String groupName, String message) {
		super(message);
		this.userName = userName;
		this.groupName = groupName;
	}

	public String getUserName() {
		return userName;
	}

	public String getGroupName() {
		return groupName;
	}

}
