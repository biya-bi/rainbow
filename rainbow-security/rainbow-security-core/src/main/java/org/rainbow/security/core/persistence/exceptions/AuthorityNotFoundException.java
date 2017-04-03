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
public class AuthorityNotFoundException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7828728173254486327L;
	private final Long authorityId;
    private final Long applicationId;

    public AuthorityNotFoundException(Long authorityId, Long applicationId) {
        this(authorityId, applicationId, String.format("No authority with Id '%d' was found for the application with Id '%d'.", authorityId, applicationId));
    }

    public AuthorityNotFoundException(Long authorityId, Long applicationId, String message) {
        super(message);
        this.authorityId = authorityId;
        this.applicationId = applicationId;
    }

    public Long getAuthorityId() {
        return authorityId;
    }

    public Long getApplicationId() {
        return applicationId;
    }
}
