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
public class UserNotInGroupException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 526740821630789747L;
	private final Long userId;
    private final Long groupId;
    private final Long applicationId;

    public UserNotInGroupException(Long userId, Long groupId, Long applicationId) {
        this(userId, groupId, applicationId, String.format("The user with ID '%d' is not in the group with ID '%d' for the application with ID '%d'.", userId, groupId, applicationId));
    }

    public UserNotInGroupException(Long userId, Long groupId, Long applicationId, String message) {
        super(message);
        this.userId = userId;
        this.groupId = groupId;
        this.applicationId = applicationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

}
