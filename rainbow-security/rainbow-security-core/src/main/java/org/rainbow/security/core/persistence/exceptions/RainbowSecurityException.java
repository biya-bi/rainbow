/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author Biya-Bi
 */
@ApplicationException(rollback = true)
public class RainbowSecurityException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7638882507004003902L;

	public RainbowSecurityException() {
	}

	public RainbowSecurityException(String message) {
		super(message);
	}

	public RainbowSecurityException(String message, Throwable cause) {
		super(message, cause);
	}

	public RainbowSecurityException(Throwable cause) {
		super(cause);
	}

	public RainbowSecurityException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
