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
public class DuplicateGroupException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3837992995580583601L;
	private final String groupName;
    private final String applicationName;

    public DuplicateGroupException(String groupName, String applicationName) {
        this(groupName, applicationName, String.format("A group with name '%s' already exists for the application with name '%s'.", groupName, applicationName));
    }

    public DuplicateGroupException(String groupName, String applicationName, String message) {
        super(message);
        this.groupName = groupName;
        this.applicationName = applicationName;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getApplicationName() {
        return applicationName;
    }

}
