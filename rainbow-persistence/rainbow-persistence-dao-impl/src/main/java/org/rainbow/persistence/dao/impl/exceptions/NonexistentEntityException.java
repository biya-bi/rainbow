package org.rainbow.persistence.dao.impl.exceptions;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class NonexistentEntityException extends Exception {
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
}
