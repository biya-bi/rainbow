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
public class ApplicationNotFoundException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5828539024675375674L;
	private final String applicationName;

    public ApplicationNotFoundException(String applicationName) {
        this(applicationName, String.format("No application with name '%s' was found.", applicationName));
    }

    public ApplicationNotFoundException( String applicationName, String message) {
        super(message);
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

}
