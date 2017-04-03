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
public class DuplicateAuthorityNameException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2878205822022281944L;
	private final String authorityName;
    private final String applicationName;

    public DuplicateAuthorityNameException(String authorityName, String applicationName) {
        this(authorityName, applicationName, String.format("An authority with name '%s' already exists for the application with name '%s'.", authorityName, applicationName));
    }

    public DuplicateAuthorityNameException(String authorityName, String applicationName, String message) {
        super(message);
        this.authorityName = authorityName;
        this.applicationName = applicationName;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public String getApplicationName() {
        return applicationName;
    }

}
