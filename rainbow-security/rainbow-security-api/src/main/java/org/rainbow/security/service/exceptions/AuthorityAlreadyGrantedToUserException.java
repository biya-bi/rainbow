package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class AuthorityAlreadyGrantedToUserException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7942051351751048123L;
	private final String authorityName;
	private final String userName;

	public AuthorityAlreadyGrantedToUserException(String authorityName, String userName) {
		this(authorityName, userName,
				String.format("The authority '%s' has already been granted to the user '%s'.", authorityName, userName));
	}

	public AuthorityAlreadyGrantedToUserException(String authorityName, String userName, String message) {
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
