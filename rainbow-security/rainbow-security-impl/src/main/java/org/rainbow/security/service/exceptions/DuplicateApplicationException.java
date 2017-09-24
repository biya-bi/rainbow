package org.rainbow.security.service.exceptions;

import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateApplicationException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4893582248322170140L;
	private final String name;

	public DuplicateApplicationException(String name) {
		this(name, String.format("An application with name '%s' already exists.", name));
	}

	public DuplicateApplicationException(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
