package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.MESSAGES;
import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.rainbow.security.core.persistence.exceptions.InvalidPasswordException;
import org.rainbow.security.core.persistence.exceptions.MembershipNotFoundException;
import org.rainbow.security.core.persistence.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.core.persistence.exceptions.PasswordHistoryException;
import org.rainbow.security.core.persistence.exceptions.WrongPasswordQuestionAnswerException;
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
	private static final String WRONG_PASSWORD_QUESTION_ANSWER_ERROR_KEY = "WrongPasswordQuestionAnswer";

	public String handle(Class<?> cls, Exception e) {
		if (cls == null)
			throw new IllegalArgumentException("The class argument cannot be null.");
		if (e == null)
			throw new IllegalArgumentException("The exception argument cannot be null.");
		
		final Logger logger = Logger.getLogger(cls.getName());

		if (e instanceof MinimumPasswordAgeViolationException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(MINIMUM_PASSWORD_AGE_VIOLATED_KEY);
		}
		if (e instanceof PasswordHistoryException) {
			logger.log(Level.WARNING, null, e);
			return String.format(ResourceBundle.getBundle(SECURITY_MESSAGES).getString(PASSWORD_HISTORY_VIOLATED_KEY),
					((PasswordHistoryException) e).getPasswordHistoryLength());
		}
		if (e instanceof InvalidPasswordException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(INVALID_PASSWORD_KEY);
		}
		if (e instanceof LockedException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(USER_LOCKED_OUT_KEY);
		}
		if (e instanceof DisabledException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(USER_DISABLED_KEY);
		}
		// TODO: Come here later and review if the MembershipNotFoundException
		// exception is properly handled.
		if ((e instanceof BadCredentialsException) || (e instanceof MembershipNotFoundException)) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(INVALID_CREDENTIALS_KEY);
		}
		if (e instanceof CredentialsExpiredException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(CREDENTIALS_EXPIRED_KEY);
		}
		if (e instanceof InvalidPasswordException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(INVALID_PASSWORD_ERROR_KEY);
		}
		if (e instanceof WrongPasswordQuestionAnswerException) {
			logger.log(Level.WARNING, null, e);
			return ResourceBundle.getBundle(SECURITY_MESSAGES).getString(WRONG_PASSWORD_QUESTION_ANSWER_ERROR_KEY);
		}
		logger.log(Level.SEVERE, null, e);
		return ResourceBundle.getBundle(MESSAGES).getString(UNEXPECTED_ERROR_KEY);

	}
}
