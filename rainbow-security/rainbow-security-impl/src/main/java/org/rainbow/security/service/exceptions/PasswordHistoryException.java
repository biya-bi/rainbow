package org.rainbow.security.service.exceptions;

import org.rainbow.security.service.exceptions.RainbowSecurityServiceException;

/**
 *
 * @author Biya-Bi
 */
public class PasswordHistoryException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -714180864430046507L;
	private int passwordHistoryThreshold;

	public PasswordHistoryException(int passwordHistoryThreshold) {
		this(passwordHistoryThreshold,
				String.format(
						"The Enforce Password History policy requires that any new password should not occur in the last '%s' used passwords.",
						passwordHistoryThreshold));
	}

	public PasswordHistoryException(int passwordHistoryThreshold, String message) {
		super(message);
		this.passwordHistoryThreshold = passwordHistoryThreshold;
	}

	public int getPasswordHistoryThreshold() {
		return passwordHistoryThreshold;
	}

}
