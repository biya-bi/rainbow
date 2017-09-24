package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class AuthorityNotFoundException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9029808352969243000L;
	private final String authorityName;

	public AuthorityNotFoundException(String authorityName) {
		this(authorityName, String.format("No authority with name '%s' was found.", authorityName));
	}

	public AuthorityNotFoundException(String authorityName, String message) {
		super(message);
		this.authorityName = authorityName;
	}

	public String getAuthorityName() {
		return authorityName;
	}

}
