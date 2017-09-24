package org.rainbow.security.service.services;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.rainbow.security.service.exceptions.DuplicateRecoveryQuestionException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.service.exceptions.RecoveryInformationNotFoundException;
import org.rainbow.security.service.exceptions.RecoveryQuestionNotFoundException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.WrongRecoveryAnswerException;

public interface UserAccountService {

	void setPassword(String userName, String password) throws UserNotFoundException, InvalidPasswordException;

	void changePassword(String userName, String oldPassword, String newPassword)
			throws UserNotFoundException, InvalidPasswordException;

	void addRecoveryQuestion(String userName, String password, String question, String answer)
			throws UserNotFoundException, DuplicateRecoveryQuestionException;

	void addRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs)
			throws UserNotFoundException, DuplicateRecoveryQuestionException;

	void resetRecoveryQuestions(String userName, String password, Map<String, String> questionAnswerPairs)
			throws UserNotFoundException;

	void changeRecoveryAnswer(String userName, String password, String question, String newAnswer)
			throws UserNotFoundException, RecoveryQuestionNotFoundException;

	void deleteRecoveryQuestion(String userName, String password, String question)
			throws UserNotFoundException, RecoveryQuestionNotFoundException;

	boolean isPasswordExpired(String userName) throws UserNotFoundException;

	boolean userExists(String userName);

	String getRecoveryQuestion(String userName) throws UserNotFoundException, RecoveryInformationNotFoundException;

	List<String> getRecoveryQuestions(String userName) throws UserNotFoundException;

	void resetPassword(String userName, String newPassword, String question, String answer)
			throws UserNotFoundException, InvalidPasswordException, WrongRecoveryAnswerException,
			RecoveryQuestionNotFoundException;

	void unlock(String userName, String question, String answer)
			throws UserNotFoundException, WrongRecoveryAnswerException, RecoveryQuestionNotFoundException;

	Date getLastLoginDate(String userName) throws UserNotFoundException;
}
