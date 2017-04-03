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
public class UserNotFoundException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2540074751720285739L;
	private final Long userId;
    private final Long applicationId;

    public UserNotFoundException(Long userId, Long applicationId) {
        this(userId, applicationId, String.format("No user with user id '%d' was found the application with id '%d'.", userId, applicationId));
    }

    public UserNotFoundException(Long userId, Long applicationId, String message) {
        super(message);
        this.userId = userId;
        this.applicationId = applicationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

}
