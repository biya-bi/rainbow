package org.rainbow.security.service.exceptions;

public class RecoveryInformationNotFoundException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5208881599969784227L;
	private final String userName;

	public RecoveryInformationNotFoundException(String userName) {
		this(userName,
				String.format("The user '%s' has no account recovery information.", userName));
	}

	public RecoveryInformationNotFoundException(String userName, String message) {
		super(message);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}

}
