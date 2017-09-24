package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class RainbowSecurityServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2838727374818214124L;

	public RainbowSecurityServiceException() {
	}

	public RainbowSecurityServiceException(String message) {
		super(message);
	}

	public RainbowSecurityServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public RainbowSecurityServiceException(Throwable cause) {
		super(cause);
	}

	public RainbowSecurityServiceException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
