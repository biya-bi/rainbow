package org.rainbow.security.service.exceptions;

import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateAuthorityException extends RainbowSecurityServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2878205822022281944L;
	private final String authorityName;

    public DuplicateAuthorityException(String authorityName) {
        this(authorityName, String.format("An authority with name '%s' already exists.", authorityName));
    }

    public DuplicateAuthorityException(String authorityName, String message) {
        super(message);
        this.authorityName = authorityName;
    }

    public String getAuthorityName() {
        return authorityName;
    }

}
