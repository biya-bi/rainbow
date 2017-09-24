package org.rainbow.security.auth.faces.controllers;

import static org.rainbow.security.auth.faces.utilities.ResourceBundles.MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rainbow.security.service.exceptions.CredentialsNotFoundException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.service.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.service.exceptions.PasswordHistoryException;
import org.rainbow.security.service.exceptions.WrongRecoveryAnswerException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationExceptionHandler {

	private static final String INVALID_CREDENTIALS_KEY = "InvalidCredentials";
	private static final String UNEXPECTED_ERROR_KEY = "UnexpectedErrorOccured";
	private static final String MINIMUM_PASSWORD_AGE_VIOLATED_KEY = "MinimumPasswordAgeViolated";
	private static final String INVALID_PASSWORD_KEY = "InvalidPassword";
	private static final String PASSWORD_HISTORY_VIOLATED_KEY = "PasswordHistoryViolated";
	private static final String USER_LOCKED_OUT_KEY = "UserLockedOutPersonal";
	private static final String USER_DISABLED_KEY = "UserDisabledPersonal";
	private static final String CREDENTIALS_EXPIRED_KEY = "UserCredentialsExpiredPersonal";
	private static final String INVALID_PASSWORD_ERROR_KEY = "InvalidPassword";
	private static final String WRONG_RECOVERY_ANSWER_ERROR_KEY = "WrongRecoveryAnswer";

	public String handle(Class<?> cls, Exception e) {
		if (cls == null)
			throw new IllegalArgumentException("The cls argument cannot be null.");
		if (e == null)
			throw new IllegalArgumentException("The e argument cannot be null.");

		final Logger logger = Logger.getLogger(cls.getName());

		if (e instanceof MinimumPasswordAgeViolationException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(MINIMUM_PASSWORD_AGE_VIOLATED_KEY);
		}
		if (e instanceof PasswordHistoryException) {
			logger.log(Level.WARNING, null, e);
			return String.format(ResourceBundle.getBundle(MESSAGES).getString(PASSWORD_HISTORY_VIOLATED_KEY),
					((PasswordHistoryException) e).getPasswordHistoryThreshold());
		}
		if (e instanceof InvalidPasswordException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(INVALID_PASSWORD_KEY);
		}
		if (e instanceof LockedException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(USER_LOCKED_OUT_KEY);
		}
		if (e instanceof DisabledException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(USER_DISABLED_KEY);
		}
		// TODO: Come here later and review if the CredentialsNotFoundException
		// exception is properly handled.
		if ((e instanceof BadCredentialsException) || (e instanceof CredentialsNotFoundException)) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(INVALID_CREDENTIALS_KEY);
		}
		if (e instanceof CredentialsExpiredException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(CREDENTIALS_EXPIRED_KEY);
		}
		if (e instanceof InvalidPasswordException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(INVALID_PASSWORD_ERROR_KEY);
		}
		if (e instanceof WrongRecoveryAnswerException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(MESSAGES).getString(WRONG_RECOVERY_ANSWER_ERROR_KEY);
		}
		logger.log(Level.SEVERE, null, e);
		return ResourceBundle.getBundle(MESSAGES).getString(UNEXPECTED_ERROR_KEY);

	}
}
