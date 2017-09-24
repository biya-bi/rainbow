package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class UserAlreadyInGroupException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7164720648933807553L;
	private final String userName;
	private final String groupName;

	public UserAlreadyInGroupException(String userName, String groupName) {
		this(userName, groupName,
				String.format("The user '%s' has already been added to the group '%s'.", userName, groupName));
	}

	public UserAlreadyInGroupException(String userName, String groupName, String message) {
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
