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
public class GroupNotFoundException extends RainbowSecurityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4442431124660064458L;
	private final Long groupId;
	private final Long applicationId;

	public GroupNotFoundException(Long groupId, Long applicationId) {
		this(groupId, applicationId, String.format("No group with Id '%d' was found for the application with Id '%d'.",
				groupId, applicationId));
	}

	public GroupNotFoundException(Long groupId, Long applicationId, String message) {
		super(message);
		this.groupId = groupId;
		this.applicationId = applicationId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public Long getApplicationId() {
		return applicationId;
	}
}
