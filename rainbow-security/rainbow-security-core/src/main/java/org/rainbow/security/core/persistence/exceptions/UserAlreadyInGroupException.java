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
public class UserAlreadyInGroupException extends RainbowSecurityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7164720648933807553L;
	private final Long userId;
	private final Long groupId;
	private final Long applicationId;

	public UserAlreadyInGroupException(Long userId, Long groupId, Long applicationId) {
		this(userId, groupId, applicationId,
				String.format(
						"A user with ID '%d' has already been added to the group with ID '%d' for the application with ID '%d'.",
						userId, groupId, applicationId));
	}

	public UserAlreadyInGroupException(Long userId, Long groupId, Long applicationId, String message) {
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
