package org.rainbow.persistence.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NonexistentEntityException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7266630048192747862L;

	public NonexistentEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public NonexistentEntityException(String message) {
		super(message);
	}

	public NonexistentEntityException(Throwable cause) {
		super(cause);
	}
}
