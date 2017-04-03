/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateApplicationNameException extends RainbowSecurityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4893582248322170140L;
	private final String name;

	public DuplicateApplicationNameException(String name) {
		this(name, String.format("An application with name '%s' already exists.", name));
	}

	public DuplicateApplicationNameException(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
