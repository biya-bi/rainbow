package org.rainbow.security.service.exceptions;

public class CredentialsNotFoundException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -867786025970273961L;
	private final String userName;

	public CredentialsNotFoundException(String userName) {
		super(String.format("No credentials were found for the user '%s'.", userName));
		this.userName = userName;
	}

	public CredentialsNotFoundException(String userName, String message) {
		super(message);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
}
