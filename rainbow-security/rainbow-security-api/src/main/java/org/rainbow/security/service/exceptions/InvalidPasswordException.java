package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class InvalidPasswordException extends RainbowSecurityServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1604836408076504303L;

	public InvalidPasswordException() {
        super("An invalid password was specified. Contact your support team for more details on the password policy.");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

}
