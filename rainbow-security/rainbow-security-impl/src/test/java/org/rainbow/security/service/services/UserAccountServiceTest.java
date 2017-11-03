package org.rainbow.security.service.services;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.security.service.encryption.SensitiveUserDetailsEncrypter;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.CredentialsNotFoundException;
import org.rainbow.security.service.exceptions.DuplicateRecoveryQuestionException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.service.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.service.exceptions.PasswordHistoryException;
import org.rainbow.security.service.exceptions.RecoveryInformationNotFoundException;
import org.rainbow.security.service.exceptions.RecoveryQuestionNotFoundException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.WrongRecoveryAnswerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 
 * @author Biya-Bi
 *
 */
@DatabaseInitialize("src/test/resources/SQL/UserAccountServiceTestSetup.sql")
public class UserAccountServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("userAccountService1")
	private UserAccountService userAccountService1;

	@Autowired
	@Qualifier("userAccountServiceWithMissingApplication")
	private UserAccountService userAccountServiceWithMissingApplication;

	@Autowired
	@Qualifier("userAccountService2")
	private UserAccountService userAccountService2;

	@Autowired
	@Qualifier("userAccountService3")
	private UserAccountService userAccountService3;

	@Autowired
	@Qualifier("userAccountService4")
	private UserAccountService userAccountService4;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	@Qualifier("authenticationManager1")
	private AuthenticationManager authenticationManager1;

	@Autowired
	@Qualifier("authenticationManager2")
	private AuthenticationManager authenticationManager2;

	@Autowired
	@Qualifier("authenticationManager3")
	private AuthenticationManager authenticationManager3;

	@Autowired
	@Qualifier("authenticationManager4")
	private AuthenticationManager authenticationManager4;

	@Autowired
	private SensitiveUserDetailsEncrypter sensitiveUserDetailsEncrypter;

	private final String missingApplication = "Missing Application";

	private static boolean encrypted = false;

	@Before
	public void encrypt() {
		if (!encrypted) {
			sensitiveUserDetailsEncrypter.encrypt();
			encrypted = true;
		}
	}

	@Test
	public void changePassword_OldPasswordIsValid_PasswordChanged() {
		final String userName = "sampleUser5";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "NewP@$$w0rd";

		// If no exception is thrown, then the password was changed
		// successfully.
		userAccountService1.changePassword(userName, oldPassword, newPassword);

		// Now we verify that the user can authenticate with the new password
		final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				newPassword);

		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = PasswordHistoryException.class)
	public void changePassword_PasswordInPasswordHistoryIsReused_ThrowPasswordHistoryException() {
		final String userName = "sampleUser6";
		final String oldPassword = "P@$$w0rd";

		userAccountService1.changePassword(userName, oldPassword, oldPassword);
	}

	@Test(expected = BadCredentialsException.class)
	public void changePassword_OldPasswordIsWrong_ThrowBadCredentialsException() {
		final String userName = "sampleUser7";
		final String oldPassword = "WrongOldP@$$w0rd";
		final String newPassword = "NewP@$$w0rd";

		userAccountService1.changePassword(userName, oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoSpecialCharacter_ThrowInvalidPasswordException() {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "Passw0rd";

		userAccountService1.changePassword(userName, oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoLowercaseCharacter_ThrowInvalidPasswordException() {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@SSW0RD";

		userAccountService1.changePassword(userName, oldPassword, newPassword);

	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoUppercaseCharacter_ThrowInvalidPasswordException() {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "p@ssw0rd";

		userAccountService1.changePassword(userName, oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoDigitCharacter_ThrowInvalidPasswordException() {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@ssword";

		userAccountService1.changePassword(userName, oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordIsTooShort_ThrowInvalidPasswordException() {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@s1";

		userAccountService1.changePassword(userName, oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordIsTooLong_ThrowInvalidPasswordException() {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLong";

		userAccountService1.changePassword(userName, oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordIsEmpty_ThrowInvalidPasswordException() {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "";

		userAccountService1.changePassword(userName, oldPassword, newPassword);
	}

	@Test
	public void resetPassword_AnswerIsCorrect_PasswordChanged() {
		final String userName = "sampleUser9";
		final String newPassword = "@Ne39assmrd";
		final String question = "What was the name of your primary school?";
		final String answer = "Sample Primary School Name";

		userAccountService1.resetPassword(userName, newPassword, question, answer);

		// Now, we verify that the user can authenticate with the new password
		SecurityContextHolder.clearContext();
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				newPassword);

		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = WrongRecoveryAnswerException.class)
	public void resetPassword_AnswerIsWrong_ThrowWrongRecoveryAnswerException() {
		final String userName = "sampleUser10";
		final String newPassword = "@Ne39assmrd";
		final String question = "What is your father's tribe?";
		final String answer = "Wrong Answer";

		try {
			userAccountService1.resetPassword(userName, newPassword, question, answer);
		} catch (WrongRecoveryAnswerException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(question, e.getQuestion());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void resetPassword_UserIsLockedOut_ThrowLockedException() {
		final String userName = "sampleUser11";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Sample Mother Name";

		userAccountService1.resetPassword(userName, newPassword, question, answer);
	}

	@Test(expected = UserNotFoundException.class)
	public void resetPassword_UserDoesNotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Sample Mother Name";

		try {
			userAccountService1.resetPassword(userName, newPassword, question, answer);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void resetPassword_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser20";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Sample Mother Name";

		try {
			userAccountServiceWithMissingApplication.resetPassword(userName, newPassword, question, answer);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void resetPassword_WrongAnswerIsAttemptedMultipleTimes_ThrowLockedException() {
		final String userName = "sampleUser12";
		final String newPassword = "@Ne39assmrd";
		final String question = "What is your dream work?";
		final String answer = "Wrong Answer";

		for (int i = 0; i < 6; i++) {
			try {
				userAccountService1.resetPassword(userName, newPassword, question, answer);
			} catch (WrongRecoveryAnswerException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	@Test
	public void changeRecoveryAnswer_CredentialsAreCorrect_RecoveryAnswerChanged() {
		final String userName = "sampleUser14";
		final String password = "P@$$w0rd";
		final String question = "What is your father's tribe?";
		final String newAnswer = "New answer to father's tribe question";
		final String newPassword = "@New9assm0rd";

		// First, we change the recovery answer.
		userAccountService1.changeRecoveryAnswer(userName, password, question, newAnswer);
		// Next we want to make sure that the user's password can be reset using
		// the new answer.
		userAccountService1.resetPassword(userName, newPassword, question, newAnswer);

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				newPassword);

		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = BadCredentialsException.class)
	public void changeRecoveryAnswer_PasswordIsWrong_ThrowBadCredentialsException() {
		final String userName = "sampleUser15";
		final String password = "Wrong Password";
		final String question = "What is your dream work?";
		final String newAnswer = "New Answer";

		userAccountService1.changeRecoveryAnswer(userName, password, question, newAnswer);
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void changeRecoveryAnswer_UserDoesNotExist_ThrowCredentialsNotFoundException() {
		final String userName = "missingUser";
		final String password = "P@$$w0rd";
		final String question = "What was the name of your primary school?";
		final String newAnswer = "New Answer";

		try {
			userAccountService1.changeRecoveryAnswer(userName, password, question, newAnswer);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void changeRecoveryAnswer_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser16";
		final String password = "P@$$w0rd";
		final String question = "What is your dream work?";
		final String newAnswer = "New Answer";
		final String missingApplicationName = "Missing Application";

		try {
			userAccountServiceWithMissingApplication.changeRecoveryAnswer(userName, password, question, newAnswer);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void changeRecoveryAnswer_UserIsLockedOut_ThrowLockedException() {
		final String userName = "sampleUser17";
		final String password = "P@$$w0rd";
		final String question = "What is your father's tribe?";
		final String newAnswer = "New Answer";

		userAccountService1.changeRecoveryAnswer(userName, password, question, newAnswer);
	}

	@Test(expected = DisabledException.class)
	public void changeRecoveryAnswer_UserIsDisabled_ThrowDisabledException() {
		final String userName = "sampleUser18";
		final String password = "P@$$w0rd";
		final String question = "What was the name of your primary school?";
		final String newAnswer = "New Answer";

		userAccountService1.changeRecoveryAnswer(userName, password, question, newAnswer);
	}

	@Test(expected = RecoveryQuestionNotFoundException.class)
	public void changeRecoveryAnswer_UserDoesNotHaveTheSpecifiedRecoveryQuestion_ThrowRecoveryQuestionNotFoundException() {
		final String userName = "sampleUser14";
		final String password = "P@$$w0rd";
		final String question = "Missing Recovery Question";
		final String newAnswer = "New Answer";

		try {
			userAccountService1.changeRecoveryAnswer(userName, password, question, newAnswer);
		} catch (RecoveryQuestionNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(question, e.getQuestion());
			throw e;
		}
	}

	@Test
	public void unlock_UserIsLockedOut_UserUnlocked() {
		final String userName = "sampleUser19";
		final String password = "P@$$w0rd";
		final String question = "Who was your childhood best friend?";
		final String answer = "Sample Childhood Best Friend";
		boolean wasLockedOut = false;

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);

		try {
			authenticationManager1.authenticate(authentication);
		} catch (LockedException e) {
			wasLockedOut = true;
		}

		Assert.assertTrue(wasLockedOut);

		userAccountService1.unlock(userName, question, answer);

		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = UserNotFoundException.class)
	public void unlock_UserDoesNotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";
		final String question = "Mother name";
		final String answer = "Sample Mother Name";
		try {
			userAccountService1.unlock(userName, question, answer);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void unlock_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser21";
		final String question = "Mother name";
		final String answer = "Sample Mother Name";

		try {
			userAccountServiceWithMissingApplication.unlock(userName, question, answer);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test
	public void isPasswordExpired_NewUserHasNotResetPassword_ReturnTrue() {
		final String userName = "sampleUser24";

		Boolean passwordExpired = userAccountService1.isPasswordExpired(userName);
		Assert.assertTrue(passwordExpired);
	}

	@Test
	public void isPasswordExpired_NewUserHasResetPassword_ReturnFalse() {
		final String userName = "sampleUser25";

		Boolean passwordExpired = userAccountService1.isPasswordExpired(userName);
		Assert.assertFalse(passwordExpired);
	}

	@Test(expected = MinimumPasswordAgeViolationException.class)
	public void changePassword_UserIsNewAndMinimumPasswordAgeIsViolated_ThrowMinimumPasswordAgeViolationException() {
		final String userName = "sampleUser26";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "C0mpo$itePassword";

		userAccountService2.changePassword(userName, oldPassword, newPassword);
	}

	@Test(expected = MinimumPasswordAgeViolationException.class)
	public void changePassword_UserIsOldAndMinimumPasswordAgeIsViolated_ThrowMinimumPasswordAgeViolationException() {
		final String userName = "sampleUser27";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "C0mpo$itePassword";

		userAccountService2.changePassword(userName, oldPassword, newPassword);
	}

	@Test
	public void getRecoveryQuestions_userHasRecoveryQuestions_RecoveryQuestionsReturned() {
		final String userName = "sampleUser28";
		final List<String> expectedQuestions = Arrays.asList("What is your father's tribe?", "What is your dream work?",
				"What was the name of your primary school?");

		final List<String> actualQuestions = userAccountService1.getRecoveryQuestions(userName);

		Assert.assertEquals(expectedQuestions.size(), actualQuestions.size());
		Assert.assertEquals(expectedQuestions.size(),
				actualQuestions.stream().filter(x -> expectedQuestions.contains(x)).count());
	}

	@Test(expected = UserNotFoundException.class)
	public void getRecoveryQuestions_UserDoesNotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";

		try {
			userAccountService1.getRecoveryQuestions(userName);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void getRecoveryQuestions_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser28";

		try {
			userAccountServiceWithMissingApplication.getRecoveryQuestions(userName);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = RecoveryInformationNotFoundException.class)
	public void getRecoveryQuestions_UserHasNoRecoveryInformation_ThrowRecoveryInformationNotFoundException() {
		final String userName = "sampleUser29";

		try {
			userAccountService1.getRecoveryQuestions(userName);
		} catch (RecoveryInformationNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void getRecoveryQuestions_UserDoesNotHaveMembership_ThrowCredentialsNotFoundException() {
		final String userName = "sampleUser30";

		try {
			userAccountService1.getRecoveryQuestions(userName);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test
	public void getRecoveryQuestion_userHasRecoveryQuestions_RandomRecoveryQuestionReturned() {
		final String userName = "sampleUser31";
		final List<String> expectedQuestions = Arrays.asList("What is your father's tribe?", "What is your dream work?",
				"What was the name of your primary school?");

		final String actualQuestion = userAccountService1.getRecoveryQuestion(userName);

		Assert.assertTrue(expectedQuestions.contains(actualQuestion));
	}

	@Test(expected = UserNotFoundException.class)
	public void getRecoveryQuestion_UserDoesNotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";

		try {
			userAccountService1.getRecoveryQuestion(userName);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void getRecoveryQuestion_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser28";

		try {
			userAccountServiceWithMissingApplication.getRecoveryQuestion(userName);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = RecoveryInformationNotFoundException.class)
	public void getRecoveryQuestion_UserHasNoRecoveryInformation_ThrowRecoveryInformationNotFoundException() {
		final String userName = "sampleUser29";

		try {
			userAccountService1.getRecoveryQuestion(userName);
		} catch (RecoveryInformationNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void getRecoveryQuestion_UserHasNoMembership_ThrowCredentialsNotFoundException() {
		final String userName = "sampleUser30";

		try {
			userAccountService1.getRecoveryQuestion(userName);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test
	public void addRecoveryQuestion_UserHasNoRecoveryQuestions_RecoveryQuestionAdded() {
		final String userName = "sampleUser32";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample mother maiden name";
		final String newPassword = "C0mpo$itePassword";

		boolean hadRecoveryQuestions = true;

		try {
			userAccountService3.getRecoveryQuestions(userName);
		} catch (RecoveryInformationNotFoundException e) {
			hadRecoveryQuestions = false;
		}

		userAccountService3.addRecoveryQuestion(userName, password, question, answer);

		final List<String> questions = userAccountService3.getRecoveryQuestions(userName);

		// If the recovery question and answer are not associated to the user, a
		// WrongRecoveryAnswerException or RecoveryQuestionNotFoundException may
		// be thrown here.
		userAccountService3.resetPassword(userName, newPassword, question, answer);

		// Let's first assert that the user had no recovery questions before.
		Assert.assertFalse(hadRecoveryQuestions);
		// Now, let's assert that the returned questions list has one
		// element.
		Assert.assertEquals(1, questions.size());
		// Next, let's check that the user's password was successfully reset
		// using the new recovery question, and that the user can authenticate
		// using the new password.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword));
	}

	@Test
	public void addRecoveryQuestion_UserHasRecoveryQuestions_RecoveryQuestionAdded() {
		final String userName = "sampleUser33";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample mother maiden name";
		final String newPassword = "C0mpo$itePassword";

		final List<String> questionsBefore = userAccountService3.getRecoveryQuestions(userName);

		userAccountService3.addRecoveryQuestion(userName, password, question, answer);

		final List<String> questionsAfter = userAccountService3.getRecoveryQuestions(userName);

		// If the recovery question and answer are not associated to the user, a
		// WrongRecoveryAnswerException or RecoveryQuestionNotFoundException may
		// be thrown here.
		userAccountService3.resetPassword(userName, newPassword, question, answer);

		// Let's first assert that the user had 3 recovery questions before.
		Assert.assertEquals(3, questionsBefore.size());
		// Now, let's assert that the returned questions list after adding the
		// new recovery question now has 4 recovery questions.
		Assert.assertEquals(4, questionsAfter.size());
		// Next, let's check that the user's password was successfully reset
		// using the new recovery question, and that the user can authenticate
		// using the new password.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword));
	}

	@Test(expected = DuplicateRecoveryQuestionException.class)
	public void addRecoveryQuestion_UserAlreadyHasRecoveryQuestion_ThrowDuplicateRecoveryQuestionException() {
		final String userName = "sampleUser34";
		final String password = "P@$$w0rd";
		final String question = "What is your dream work?";
		final String answer = "Sample answer";
		try {
			userAccountService3.addRecoveryQuestion(userName, password, question, answer);
		} catch (DuplicateRecoveryQuestionException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(question, e.getQuestion());
			throw e;
		}
	}

	@Test(expected = UserNotFoundException.class)
	public void addRecoveryQuestion_UserDoesnotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample answer";
		try {
			userAccountService3.addRecoveryQuestion(userName, password, question, answer);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void addRecoveryQuestion_UserDoesNotHaveMembership_ThrowCredentialsNotFoundException() {
		final String userName = "sampleUser35";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample answer";
		try {
			userAccountService3.addRecoveryQuestion(userName, password, question, answer);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void addRecoveryQuestion_ApplicationDoesnotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser33";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample answer";
		try {
			userAccountServiceWithMissingApplication.addRecoveryQuestion(userName, password, question, answer);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void addRecoveryQuestion_UserIsLockedOut_ThrowLockedException() {
		final String userName = "sampleUser38";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample mother maiden name";

		userAccountService3.addRecoveryQuestion(userName, password, question, answer);
	}

	@Test(expected = DisabledException.class)
	public void addRecoveryQuestion_UserIsDisabled_ThrowDisabledException() {
		final String userName = "sampleUser39";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample mother maiden name";

		userAccountService3.addRecoveryQuestion(userName, password, question, answer);
	}

	@Test(expected = BadCredentialsException.class)
	public void addRecoveryQuestion_PasswordIsWrong_ThrowBadCredentialsException() {
		final String userName = "sampleUser40";
		final String password = "Wrong Password";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample mother maiden name";

		userAccountService3.addRecoveryQuestion(userName, password, question, answer);
	}

	@Test(expected = LockedException.class)
	public void addRecoveryQuestion_WrongPasswordIsAttemptedMultipleTimes_ThrowLockedException() {
		final String userName = "sampleUser41";
		final String password = "Wrong Password";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample mother maiden name";

		for (int i = 0; i < 6; i++) {
			try {
				userAccountService3.addRecoveryQuestion(userName, password, question, answer);
			} catch (BadCredentialsException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	@Test(expected = CredentialsExpiredException.class)
	public void addRecoveryQuestion_PasswordHasExpired_ThrowCredentialsExpiredException() {
		final String userName = "sampleUser42";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		final String answer = "Sample mother maiden name";

		userAccountService1.addRecoveryQuestion(userName, password, question, answer);
	}

	@Test
	public void deleteRecoveryQuestion_UserHasRecoveryQuestions_RecoveryQuestionDeleted() {
		final String userName = "sampleUser36";
		final String password = "P@$$w0rd";
		final String question = "What was the name of your primary school?";

		final List<String> questionsBefore = userAccountService3.getRecoveryQuestions(userName);

		userAccountService3.deleteRecoveryQuestion(userName, password, question);

		final List<String> questionsAfter = userAccountService3.getRecoveryQuestions(userName);

		Assert.assertTrue(questionsBefore.contains(question));
		Assert.assertEquals(3, questionsBefore.size());
		Assert.assertEquals(2, questionsAfter.size());
		Assert.assertFalse(questionsAfter.contains(question));
	}

	@Test(expected = RecoveryQuestionNotFoundException.class)
	public void deleteRecoveryQuestion_UserDoesNotHaveRecoveryQuestion_ThrowRecoveryQuestionNotFoundException() {
		final String userName = "sampleUser37";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		try {
			userAccountService3.deleteRecoveryQuestion(userName, password, question);
		} catch (RecoveryQuestionNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(question, e.getQuestion());
			throw e;
		}
	}

	@Test(expected = UserNotFoundException.class)
	public void deleteRecoveryQuestion_UserDoesnotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		try {
			userAccountService3.deleteRecoveryQuestion(userName, password, question);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void deleteRecoveryQuestion_UserDoesNotHaveMembership_ThrowCredentialsNotFoundException() {
		final String userName = "sampleUser35";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		try {
			userAccountService3.deleteRecoveryQuestion(userName, password, question);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void deleteRecoveryQuestion_ApplicationDoesnotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser33";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";
		try {
			userAccountServiceWithMissingApplication.deleteRecoveryQuestion(userName, password, question);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void deleteRecoveryQuestion_UserIsLockedOut_ThrowLockedException() {
		final String userName = "sampleUser43";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";

		userAccountService3.deleteRecoveryQuestion(userName, password, question);
	}

	@Test(expected = DisabledException.class)
	public void deleteRecoveryQuestion_UserIsDisabled_ThrowDisabledException() {
		final String userName = "sampleUser44";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";

		userAccountService3.deleteRecoveryQuestion(userName, password, question);
	}

	@Test(expected = BadCredentialsException.class)
	public void deleteRecoveryQuestion_PasswordIsWrong_ThrowBadCredentialsException() {
		final String userName = "sampleUser45";
		final String password = "Wrong Password";
		final String question = "What is your mother's maiden name?";

		userAccountService3.deleteRecoveryQuestion(userName, password, question);
	}

	@Test(expected = LockedException.class)
	public void deleteRecoveryQuestion_WrongPasswordIsAttemptedMultipleTimes_ThrowLockedException() {
		final String userName = "sampleUser46";
		final String password = "Wrong Password";
		final String question = "What is your mother's maiden name?";

		for (int i = 0; i < 6; i++) {
			try {
				userAccountService3.deleteRecoveryQuestion(userName, password, question);
			} catch (BadCredentialsException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	@Test(expected = CredentialsExpiredException.class)
	public void deleteRecoveryQuestion_PasswordHasExpired_ThrowCredentialsExpiredException() {
		final String userName = "sampleUser47";
		final String password = "P@$$w0rd";
		final String question = "What is your mother's maiden name?";

		userAccountService1.deleteRecoveryQuestion(userName, password, question);
	}

	@Test
	public void getLastLoginDate_UserAuthenticatesOne_ReturnNull() {
		final String userName = "sampleUser48";
		final String password = "P@$$w0rd";
		final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);

		authenticationManager4.authenticate(authentication);
		final Date lastLoginDate = userAccountService4.getLastLoginDate(userName);

		// After the first authentication, last login date is null.
		Assert.assertNull(lastLoginDate);

	}

	@Test
	public void getLastLoginDate_UserAuthenticatesTwice_LastLoginDateReturned() {
		final String userName = "sampleUser49";
		final String password = "P@$$w0rd";
		final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);

		Date lastLoginDate = null;
		for (int i = 0; i < 2; i++) {
			authenticationManager4.authenticate(authentication);
			lastLoginDate = userAccountService4.getLastLoginDate(userName);
		}

		// After two authentications, last login date is no more null.
		Assert.assertNotNull(lastLoginDate);
	}

	@Test(expected = UserNotFoundException.class)
	public void getLastLoginDate_UserDoesNotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";

		try {
			userAccountService4.getLastLoginDate(userName);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void getLastLoginDate_UserDoesNotHaveCredentials_ThrowCredentialsNotFoundException() {
		final String userName = "sampleUser50";

		try {
			userAccountService4.getLastLoginDate(userName);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void getLastLoginDate_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser20";

		try {
			userAccountServiceWithMissingApplication.getLastLoginDate(userName);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test
	public void resetRecoveryQuestions_UserHasNoRecoveryQuestions_RecoveryQuestionsReset() {
		final String userName = "sampleUser51";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		final String newPassword1 = "C0mpo$itePassword1";
		final String newPassword2 = "C0mpo$itePassword2";
		final String newPassword3 = "C0mpo$itePassword3";

		boolean hadRecoveryQuestions = true;

		try {
			userAccountService3.getRecoveryQuestions(userName);
		} catch (RecoveryInformationNotFoundException e) {
			hadRecoveryQuestions = false;
		}

		userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);

		final List<String> questions = userAccountService3.getRecoveryQuestions(userName);

		// Let's first assert that the user had no recovery questions before.
		Assert.assertFalse(hadRecoveryQuestions);

		// Now, let's assert that the returned questions list has three
		// elements.
		Assert.assertEquals(3, questions.size());

		// Let's now attempt to reset the user's password using question1 and
		// answer1.
		userAccountService3.resetPassword(userName, newPassword1, question1, answer1);

		// Next, let's check that the user's password was successfully reset
		// using the question1 and answer1, and that the user can authenticate
		// using the password1.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword1));

		// Let's now attempt to reset the user's password using question2 and
		// answer2.
		userAccountService3.resetPassword(userName, newPassword2, question2, answer2);

		// Next, let's check that the user's password was successfully reset
		// using the question2 and answer2, and that the user can authenticate
		// using the password2.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword2));

		// Let's now attempt to reset the user's password using question3 and
		// answer3.
		userAccountService3.resetPassword(userName, newPassword3, question3, answer3);

		// Next, let's check that the user's password was successfully reset
		// using the question3 and answer3, and that the user can authenticate
		// using the password3.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword3));
	}

	@Test
	public void resetRecoveryQuestions_AnOldRecoveryQuestionAppearsAmongNewRecoveryQuestions_RecoveryQuestionsReset() {
		final String userName = "sampleUser52";
		final String password = "P@$$w0rd";
		final String question1 = "What was the name of your primary school?";
		final String answer1 = "Sample primary school name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		final String newPassword1 = "C0mpo$itePassword1";
		final String newPassword2 = "C0mpo$itePassword2";
		final String newPassword3 = "C0mpo$itePassword3";

		final List<String> oldQuestions = userAccountService3.getRecoveryQuestions(userName);

		userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);

		final List<String> newQuestions = userAccountService3.getRecoveryQuestions(userName);

		// Let's first assert that the user had one recovery question before.
		Assert.assertEquals(1, oldQuestions.size());

		// Now, let's assert that the returned questions list has three
		// elements.
		Assert.assertEquals(3, newQuestions.size());

		// Let's now attempt to reset the user's password using question1 and
		// answer1.
		userAccountService3.resetPassword(userName, newPassword1, question1, answer1);

		// Next, let's check that the user's password was successfully reset
		// using the question1 and answer1, and that the user can authenticate
		// using the password1.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword1));

		// Let's now attempt to reset the user's password using question2 and
		// answer2.
		userAccountService3.resetPassword(userName, newPassword2, question2, answer2);

		// Next, let's check that the user's password was successfully reset
		// using the question2 and answer2, and that the user can authenticate
		// using the password2.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword2));

		// Let's now attempt to reset the user's password using question3 and
		// answer3.
		userAccountService3.resetPassword(userName, newPassword3, question3, answer3);

		// Next, let's check that the user's password was successfully reset
		// using the question3 and answer3, and that the user can authenticate
		// using the password3.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword3));
	}

	@Test
	public void resetRecoveryQuestions_NoOldRecoveryQuestionAppearsAmongNewRecoveryQuestions_RecoveryQuestionsReset() {
		final String userName = "sampleUser69";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		final String newPassword1 = "C0mpo$itePassword1";
		final String newPassword2 = "C0mpo$itePassword2";
		final String newPassword3 = "C0mpo$itePassword3";

		final List<String> oldQuestions = userAccountService3.getRecoveryQuestions(userName);

		userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);

		final List<String> newQuestions = userAccountService3.getRecoveryQuestions(userName);

		// Let's first assert that the user had one recovery question before.
		Assert.assertEquals(1, oldQuestions.size());

		// Now, let's assert that the returned questions list has three
		// elements.
		Assert.assertEquals(3, newQuestions.size());

		Assert.assertFalse(newQuestions.contains(oldQuestions.get(0)));

		// Let's now attempt to reset the user's password using question1 and
		// answer1.
		userAccountService3.resetPassword(userName, newPassword1, question1, answer1);

		// Next, let's check that the user's password was successfully reset
		// using the question1 and answer1, and that the user can authenticate
		// using the password1.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword1));

		// Let's now attempt to reset the user's password using question2 and
		// answer2.
		userAccountService3.resetPassword(userName, newPassword2, question2, answer2);

		// Next, let's check that the user's password was successfully reset
		// using the question2 and answer2, and that the user can authenticate
		// using the password2.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword2));

		// Let's now attempt to reset the user's password using question3 and
		// answer3.
		userAccountService3.resetPassword(userName, newPassword3, question3, answer3);

		// Next, let's check that the user's password was successfully reset
		// using the question3 and answer3, and that the user can authenticate
		// using the password3.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword3));
	}

	@Test(expected = UserNotFoundException.class)
	public void resetRecoveryQuestions_UserDoesnotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		try {
			userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void resetRecoveryQuestions_UserDoesNotHaveMembership_ThrowCredentialsNotFoundException() {
		final String userName = "sampleUser53";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		try {
			userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void resetRecoveryQuestions_ApplicationDoesnotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser53";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		try {
			userAccountServiceWithMissingApplication.resetRecoveryQuestions(userName, password, questionAnswerPairs);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void resetRecoveryQuestions_UserIsLockedOut_ThrowLockedException() {
		final String userName = "sampleUser54";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);
	}

	@Test(expected = DisabledException.class)
	public void resetRecoveryQuestions_UserIsDisabled_ThrowDisabledException() {
		final String userName = "sampleUser55";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);
	}

	@Test(expected = BadCredentialsException.class)
	public void resetRecoveryQuestions_PasswordIsWrong_ThrowBadCredentialsException() {
		final String userName = "sampleUser56";
		final String password = "Wrong Password";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);
	}

	@Test(expected = LockedException.class)
	public void resetRecoveryQuestions_WrongPasswordIsAttemptedMultipleTimes_ThrowLockedException() {
		final String userName = "sampleUser57";
		final String password = "Wrong Password";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		for (int i = 0; i < 6; i++) {
			try {
				userAccountService3.resetRecoveryQuestions(userName, password, questionAnswerPairs);
			} catch (BadCredentialsException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			}
		}

	}

	@Test(expected = CredentialsExpiredException.class)
	public void resetRecoveryQuestions_PasswordHasExpired_ThrowCredentialsExpiredException() {
		final String userName = "sampleUser58";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService1.resetRecoveryQuestions(userName, password, questionAnswerPairs);
	}

	@Test
	public void addRecoveryQuestions_UserHasNoRecoveryQuestions_RecoveryQuestionsAdded() {
		final String userName = "sampleUser59";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		final String newPassword1 = "C0mpo$itePassword1";
		final String newPassword2 = "C0mpo$itePassword2";
		final String newPassword3 = "C0mpo$itePassword3";

		boolean hadRecoveryQuestions = true;

		try {
			userAccountService3.getRecoveryQuestions(userName);
		} catch (RecoveryInformationNotFoundException e) {
			hadRecoveryQuestions = false;
		}

		userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);

		final List<String> questions = userAccountService3.getRecoveryQuestions(userName);

		// Let's first assert that the user had no recovery questions before.
		Assert.assertFalse(hadRecoveryQuestions);

		// Now, let's assert that the returned questions list has three
		// elements.
		Assert.assertEquals(3, questions.size());

		// Let's now attempt to reset the user's password using question1 and
		// answer1.
		userAccountService3.resetPassword(userName, newPassword1, question1, answer1);

		// Next, let's check that the user's password was successfully reset
		// using the question1 and answer1, and that the user can authenticate
		// using the password1.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword1));

		// Let's now attempt to reset the user's password using question2 and
		// answer2.
		userAccountService3.resetPassword(userName, newPassword2, question2, answer2);

		// Next, let's check that the user's password was successfully reset
		// using the question2 and answer2, and that the user can authenticate
		// using the password2.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword2));

		// Let's now attempt to reset the user's password using question3 and
		// answer3.
		userAccountService3.resetPassword(userName, newPassword3, question3, answer3);

		// Next, let's check that the user's password was successfully reset
		// using the question3 and answer3, and that the user can authenticate
		// using the password3.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword3));
	}

	@Test
	public void addRecoveryQuestions_UserHasRecoveryQuestions_RecoveryQuestionAdded() {
		final String userName = "sampleUser60";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		final String newPassword1 = "C0mpo$itePassword1";
		final String newPassword2 = "C0mpo$itePassword2";
		final String newPassword3 = "C0mpo$itePassword3";

		final List<String> oldQuestions = userAccountService3.getRecoveryQuestions(userName);

		userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);

		final List<String> newQuestions = userAccountService3.getRecoveryQuestions(userName);

		// Let's first assert that the user had one recovery question before.
		Assert.assertEquals(1, oldQuestions.size());

		// Now, let's assert that the returned questions list has four
		// elements.
		Assert.assertEquals(4, newQuestions.size());

		Assert.assertTrue(newQuestions.contains(oldQuestions.get(0)));

		// Let's now attempt to reset the user's password using question1 and
		// answer1.
		userAccountService3.resetPassword(userName, newPassword1, question1, answer1);

		// Next, let's check that the user's password was successfully reset
		// using the question1 and answer1, and that the user can authenticate
		// using the password1.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword1));

		// Let's now attempt to reset the user's password using question2 and
		// answer2.
		userAccountService3.resetPassword(userName, newPassword2, question2, answer2);

		// Next, let's check that the user's password was successfully reset
		// using the question2 and answer2, and that the user can authenticate
		// using the password2.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword2));

		// Let's now attempt to reset the user's password using question3 and
		// answer3.
		userAccountService3.resetPassword(userName, newPassword3, question3, answer3);

		// Next, let's check that the user's password was successfully reset
		// using the question3 and answer3, and that the user can authenticate
		// using the password3.
		authenticationManager3.authenticate(new UsernamePasswordAuthenticationToken(userName, newPassword3));
	}

	@Test(expected = DuplicateRecoveryQuestionException.class)
	public void addRecoveryQuestions_UserAlreadyHasRecoveryQuestion_ThrowDuplicateRecoveryQuestionException() {
		final String userName = "sampleUser61";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "What is your father's tribe?";
		final String answer3 = "Sample father's tribe";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		try {
			userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);
		} catch (DuplicateRecoveryQuestionException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(question3, e.getQuestion());
			throw e;
		}
	}

	@Test(expected = UserNotFoundException.class)
	public void addRecoveryQuestions_UserDoesnotExist_ThrowUserNotFoundException() {
		final String userName = "missingUser";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		try {
			userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}

	}

	@Test(expected = CredentialsNotFoundException.class)
	public void addRecoveryQuestions_UserDoesNotHaveMembership_ThrowCredentialsNotFoundException() {
		final String userName = "sampleUser62";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		try {
			userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);
		} catch (CredentialsNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			throw e;
		}

	}

	@Test(expected = ApplicationNotFoundException.class)
	public void addRecoveryQuestions_ApplicationDoesnotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser63";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		try {
			userAccountServiceWithMissingApplication.addRecoveryQuestions(userName, password, questionAnswerPairs);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void addRecoveryQuestions_UserIsLockedOut_ThrowLockedException() {
		final String userName = "sampleUser64";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);
	}

	@Test(expected = DisabledException.class)
	public void addRecoveryQuestions_UserIsDisabled_ThrowDisabledException() {
		final String userName = "sampleUser65";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);
	}

	@Test(expected = BadCredentialsException.class)
	public void addRecoveryQuestions_PasswordIsWrong_ThrowBadCredentialsException() {
		final String userName = "sampleUser66";
		final String password = "Wrong Password";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);
	}

	@Test(expected = LockedException.class)
	public void addRecoveryQuestions_WrongPasswordIsAttemptedMultipleTimes_ThrowLockedException() {
		final String userName = "sampleUser67";
		final String password = "Wrong Password";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		for (int i = 0; i < 6; i++) {
			try {
				userAccountService3.addRecoveryQuestions(userName, password, questionAnswerPairs);
			} catch (BadCredentialsException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	@Test(expected = CredentialsExpiredException.class)
	public void addRecoveryQuestions_PasswordHasExpired_ThrowCredentialsExpiredException() {
		final String userName = "sampleUser68";
		final String password = "P@$$w0rd";
		final String question1 = "What is your mother's maiden name?";
		final String answer1 = "Sample mother maiden name";
		final String question2 = "What is your dream work?";
		final String answer2 = "Sample dream work";
		final String question3 = "In which town were you born?";
		final String answer3 = "Sample birth town";

		final Map<String, String> questionAnswerPairs = new HashMap<>();
		questionAnswerPairs.put(question1, answer1);
		questionAnswerPairs.put(question2, answer2);
		questionAnswerPairs.put(question3, answer3);

		userAccountService1.addRecoveryQuestions(userName, password, questionAnswerPairs);
	}

}