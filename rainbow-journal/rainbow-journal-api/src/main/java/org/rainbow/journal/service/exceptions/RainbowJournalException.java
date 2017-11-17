package org.rainbow.journal.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
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
