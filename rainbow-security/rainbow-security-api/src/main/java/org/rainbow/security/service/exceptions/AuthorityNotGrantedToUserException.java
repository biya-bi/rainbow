package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class AuthorityNotGrantedToUserException extends RainbowSecurityServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 299986844289042694L;
	private final String authorityName;
	private final String userName;

	public AuthorityNotGrantedToUserException(String authorityName, String userNameName) {
		this(authorityName, userNameName, String.format("The authority '%s' has not been granted to the user '%s'.",
				authorityName, userNameName));
	}

	public AuthorityNotGrantedToUserException(String authorityName, String userName, String message) {
		super(message);
		this.authorityName = authorityName;
		this.userName = userName;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public String getUserName() {
		return userName;
	}

}
