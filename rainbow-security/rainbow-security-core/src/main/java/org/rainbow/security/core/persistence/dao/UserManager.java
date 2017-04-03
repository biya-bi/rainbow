package org.rainbow.security.core.persistence.dao;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.InvalidPasswordException;
import org.rainbow.security.core.persistence.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.core.persistence.exceptions.PasswordHistoryException;
import org.rainbow.security.core.persistence.exceptions.WrongPasswordQuestionAnswerException;
import org.springframework.security.core.AuthenticationException;

public interface UserManager extends Dao<User, Long, SearchOptions> {

	/**
	 * Get the name of the application for which the user management applies.
	 * 
	 * @return the application for which the user management applies.
	 */
	String getApplicationName();

	void setPassword(String userName, String password) throws InvalidPasswordException, PasswordHistoryException;

	/**
	 * Modify the current user's password.
	 * 
	 * @param oldPassword
	 *            the current user's password
	 * @param newPassword
	 *            the password to change to
	 * @throws AuthenticationException
	 *             if the old credentials could not be validated
	 * @throws InvalidPasswordException
	 *             if the new password is invalid
	 * @throws PasswordHistoryException
	 *             if the new password appears in the password history. i.e. if
	 *             the new password has already been used
	 * @throws MinimumPasswordAgeViolationException
	 *             if the minimum number of days required for password change
	 *             has not been reached
	 */
	void changePassword(String oldPassword, String newPassword) throws AuthenticationException,
			InvalidPasswordException, PasswordHistoryException, MinimumPasswordAgeViolationException;

	String resetPassword(String passwordQuestionAnswer)
			throws AuthenticationException, WrongPasswordQuestionAnswerException;

	void changePasswordQuestionAndAnswer(String password, String newPasswordQuestion, String newPasswordQuestionAnswer)
			throws AuthenticationException;

	void unlock(String userName) throws AuthenticationException;

	void delete(String userName) throws AuthenticationException;

	boolean passwordExpired(String userName) throws AuthenticationException;
}
