package org.rainbow.security.core.persistence.exceptions;

import org.springframework.security.core.AuthenticationException;

public class MembershipNotFoundException extends AuthenticationException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6012400310920931270L;
	private final String userName;
	private final String applicationName;

	public MembershipNotFoundException(String userName, String applicationName) {
		this(userName, applicationName, String.format(
				"No membership for the user with user name '%s' was found in the application with name '%s'.", userName, applicationName));
	}

	public MembershipNotFoundException(String userName, String applicationName, String message) {
		super(message);
		this.userName = userName;
		this.applicationName = applicationName;
	}

	public String getUserName() {
		return userName;
	}

	public String getApplicationName() {
		return applicationName;
	}

}
