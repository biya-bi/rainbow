package org.rainbow.security.service.exceptions;

import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;

public class ApplicationNotFoundException extends RainbowSecurityServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8448542800890806590L;
	private final String applicationName;

	public ApplicationNotFoundException(String applicationName) {
		this(applicationName, String.format("No application with name '%s' was found.", applicationName));
	}

	public ApplicationNotFoundException(String applicationName, String message) {
		super(message);
		this.applicationName = applicationName;
	}

	public String getApplicationName() {
		return applicationName;
	}

}
