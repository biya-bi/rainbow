package org.rainbow.security.core.service;

import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.InvalidPasswordException;
import org.rainbow.security.core.persistence.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.core.persistence.exceptions.PasswordHistoryException;
import org.rainbow.security.core.persistence.exceptions.WrongPasswordQuestionAnswerException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends Service<User, Long, SearchOptions> {

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

	void changePasswordQuestionAndAnswer(String password, String newPasswordQuestion, String newPasswordQuestionAnswer)
			throws AuthenticationException;

	void delete(String userName) throws AuthenticationException;

	boolean passwordExpired(String userName) throws AuthenticationException;

	boolean userExists(String userName);

	String getSecurityQuestion(String userName) throws UsernameNotFoundException;

	void resetPassword(String userName, String newPassword, String question, String answer)
			throws InvalidPasswordException, LockedException, DisabledException, WrongPasswordQuestionAnswerException;

	void unlock(String userName, String question, String answer)
			throws UsernameNotFoundException, DisabledException, WrongPasswordQuestionAnswerException;
}
