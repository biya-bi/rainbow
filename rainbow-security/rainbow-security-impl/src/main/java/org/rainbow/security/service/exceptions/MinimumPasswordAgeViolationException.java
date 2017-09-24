package org.rainbow.security.service.exceptions;

import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;

/**
 *
 * @author Biya-Bi
 */
public class MinimumPasswordAgeViolationException extends RainbowSecurityServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 670286918425635879L;

	public MinimumPasswordAgeViolationException() {
        super("A password cannot be change if it has not been used for at least the number of days specified in the Minimum Password Age policy.");
    }

    public MinimumPasswordAgeViolationException(String message) {
        super(message);
    }
    
}
