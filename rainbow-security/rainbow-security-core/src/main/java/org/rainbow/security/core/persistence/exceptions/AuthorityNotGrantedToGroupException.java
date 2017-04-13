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
public class AuthorityNotGrantedToGroupException extends RainbowSecurityException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6414483462409419299L;
	private final Long authorityId;
	private final Long groupId;
	private final Long applicationId;

	public AuthorityNotGrantedToGroupException(Long authorityId, Long groupId, Long applicationId) {
		this(authorityId, groupId, applicationId,
				String.format(
						"The authority with ID '%d' has not been granted to the group with ID '%d' for the application with ID '%d'.",
						authorityId, groupId, applicationId));
	}

	public AuthorityNotGrantedToGroupException(Long authorityId, Long groupId, Long applicationId, String message) {
		super(message);
		this.authorityId = authorityId;
		this.groupId = groupId;
		this.applicationId = applicationId;
	}

	public Long getAuthorityId() {
		return authorityId;
	}

	public Long getGroupId() {
		return groupId;
	}

	public Long getApplicationId() {
		return applicationId;
	}

}