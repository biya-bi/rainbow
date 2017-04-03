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
public class DuplicateUserException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3920977178921604470L;
	private final String userName;
    private final String applicationName;

    public DuplicateUserException(String userName, String applicationName) {
        this(userName, applicationName, String.format("A user with user name '%s' already exists for the application with name '%s'.", userName, applicationName));
    }

    public DuplicateUserException(String userName, String applicationName, String message) {
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
