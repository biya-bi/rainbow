package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateUserException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3920977178921604470L;
	private final String userName;

	public DuplicateUserException(String userName) {
		this(userName, String.format("A user with user name '%s' already exists.", userName));
	}

	public DuplicateUserException(String userName, String message) {
		super(message);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

}
