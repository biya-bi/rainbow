/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.core.persistence.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author Biya-Bi
 */
@ApplicationException(rollback = true)
public class RainbowJournalException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2302908330419228601L;

	public RainbowJournalException() {
    }

    public RainbowJournalException(String message) {
        super(message);
    }

    public RainbowJournalException(String message, Throwable cause) {
        super(message, cause);
    }

    public RainbowJournalException(Throwable cause) {
        super(cause);
    }

    public RainbowJournalException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
