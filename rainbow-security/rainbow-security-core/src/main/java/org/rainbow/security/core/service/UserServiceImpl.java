package org.rainbow.security.core.service;

import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.dao.UserManager;
import org.rainbow.security.core.persistence.exceptions.InvalidPasswordException;
import org.rainbow.security.core.persistence.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.core.persistence.exceptions.PasswordHistoryException;
import org.rainbow.security.core.persistence.exceptions.WrongPasswordQuestionAnswerException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

public class UserServiceImpl extends RainbowSecurityService<User, Long, SearchOptions> implements UserService {

	public UserServiceImpl(UserManager dao) {
		super(dao);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = AuthenticationException.class)
	public void setPassword(String userName, String password)
			throws InvalidPasswordException, PasswordHistoryException {
		((UserManager) this.getDao()).setPassword(userName, password);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = AuthenticationException.class)
	public void changePassword(String oldPassword, String newPassword)
			throws AuthenticationException, InvalidPasswordException,
			PasswordHistoryException, MinimumPasswordAgeViolationException {
		((UserManager) this.getDao()).changePassword(oldPassword, newPassword);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = { AuthenticationException.class,
			WrongPasswordQuestionAnswerException.class })
	public String resetPassword(String passwordQuestionAnswer)
			throws AuthenticationException, WrongPasswordQuestionAnswerException {
		return ((UserManager) this.getDao()).resetPassword(passwordQuestionAnswer);
	}

	@Override
	@Transactional(readOnly = false, noRollbackFor = AuthenticationException.class)
	public void changePasswordQuestionAndAnswer(String password, String newPasswordQuestion,
			String newPasswordQuestionAnswer) throws AuthenticationException {
		((UserManager) this.getDao()).changePasswordQuestionAndAnswer(password, newPasswordQuestion,
				newPasswordQuestionAnswer);
	}

	@Override
	@Transactional(readOnly = false)
	public void unlock(String userName) throws AuthenticationException {
		((UserManager) this.getDao()).unlock(userName);
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(String userName) throws AuthenticationException {
		((UserManager) this.getDao()).delete(userName);
	}

	@Override
	@Transactional(readOnly = true)
	public boolean passwordExpired(String userName) throws AuthenticationException {
		return ((UserManager) this.getDao()).passwordExpired(userName);
	}
}