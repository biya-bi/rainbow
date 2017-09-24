package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class UserNotFoundException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2540074751720285739L;
	private final String userName;

	public UserNotFoundException(String userName) {
		this(userName, String.format("No user with user name '%s' was found.", userName));
	}

	public UserNotFoundException(String userName, String message) {
		super(message);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

}
