package org.rainbow.security.core.service;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Membership;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.ApplicationNotFoundException;
import org.rainbow.security.core.persistence.exceptions.DuplicateUserException;
import org.rainbow.security.core.persistence.exceptions.InvalidPasswordException;
import org.rainbow.security.core.persistence.exceptions.MembershipNotFoundException;
import org.rainbow.security.core.persistence.exceptions.MinimumPasswordAgeViolationException;
import org.rainbow.security.core.persistence.exceptions.PasswordHistoryException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundNameException;
import org.rainbow.security.core.persistence.exceptions.WrongPasswordQuestionAnswerException;
import org.rainbow.security.core.utilities.EntityManagerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 * @author Biya-Bi
 *
 */
@DatabaseInitialize("src/test/resources/UserServiceTestSetup.sql")
public class UserServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("userServiceWithMissingApplication")
	private UserService userServiceWithMissingApplication;

	@Autowired
	@Qualifier("userService1")
	private UserService userService1;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("authenticationManager1")
	private AuthenticationManager authenticationManager1;

	private final String testApplicationName = "Test Application";
	private final String missingApplicationName = "Missing Application";

	@Test
	public void create_UserIsValid_UserCreated() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "newUser";
		final String password = "P@$$w0rd";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);

		User actual = em.getReference(User.class, expected.getId());
		Assert.assertNotNull(actual);
		Assert.assertEquals(expected.getUserName(), actual.getUserName());
		Membership actualMembership = actual.getMembership();
		Assert.assertNotNull(actualMembership);
		Assert.assertEquals(expectedMembership.isEnabled(), actualMembership.isEnabled());
		Assert.assertEquals(expectedMembership.isLockedOut(), actualMembership.isLockedOut());
	}

	@Test(expected = DuplicateUserException.class)
	public void create_UserAlreadyExist_ThrowDuplicateUserException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "sampleUser1";
		final String password = "P@$$w0rd";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		try {
			userService.create(expected);
		} catch (DuplicateUserException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(testApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = InvalidPasswordException.class)
	public void create_PasswordHasNoSpecialCharacter_ThrowInvalidPasswordException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "userWithInvalidPassword";
		final String password = "Passw0rd";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

	@Test(expected = InvalidPasswordException.class)
	public void create_PasswordHasNoLowercaseCharacter_ThrowInvalidPasswordException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "userWithInvalidPassword";
		final String password = "P@SSW0RD";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

	@Test(expected = InvalidPasswordException.class)
	public void create_PasswordHasNoUppercaseCharacter_ThrowInvalidPasswordException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "userWithInvalidPassword";
		final String password = "p@ssw0rd";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

	@Test(expected = InvalidPasswordException.class)
	public void create_PasswordHasNoDigitCharacter_ThrowInvalidPasswordException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "userWithInvalidPassword";
		final String password = "P@ssword";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

	@Test(expected = InvalidPasswordException.class)
	public void create_PasswordIsTooShort_ThrowInvalidPasswordException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "userWithInvalidPassword";
		final String password = "P@s1";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

	@Test(expected = InvalidPasswordException.class)
	public void create_PasswordIsTooLong_ThrowInvalidPasswordException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "userWithInvalidPassword";
		final String password = "P@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLong";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

	@Test(expected = InvalidPasswordException.class)
	public void create_PasswordIsEmpty_ThrowInvalidPasswordException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "userWithInvalidPassword";
		final String password = "";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expectedMembership.setPasswordQuestion("Mother name");
		expectedMembership.setPasswordQuestionAnswer("Nondo Lydienne");
		expectedMembership.setApplication(application);
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

	@Test
	public void changePassword_OldPasswordIsValid_PasswordChanged()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser5";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "NewP@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// If no exception is thrown, then the password was changed
		// successfully.
		userService.changePassword(oldPassword, newPassword);

		// Now we verify that the user can authenticate with the new password
		SecurityContextHolder.clearContext();
		authentication = new UsernamePasswordAuthenticationToken(userName, newPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		authenticationManager.authenticate(authentication);
	}

	@Test(expected = PasswordHistoryException.class)
	public void changePassword_PasswordInPasswordHistoryIsReused_ThrowPasswordHistoryException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser6";
		String oldPassword = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePassword(oldPassword, oldPassword);
	}

	@Test(expected = BadCredentialsException.class)
	public void changePassword_OldPasswordIsWrong_ThrowBadCredentialsException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser7";
		final String oldPassword = "WrongOldP@$$w0rd";
		final String newPassword = "NewP@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePassword(oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoSpecialCharacter_ThrowInvalidPasswordException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "Passw0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		userService.changePassword(oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoLowercaseCharacter_ThrowInvalidPasswordException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@SSW0RD";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePassword(oldPassword, newPassword);

	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoUppercaseCharacter_ThrowInvalidPasswordException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "p@ssw0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		userService.changePassword(oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordHasNoDigitCharacter_ThrowInvalidPasswordException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@ssword";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePassword(oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordIsTooShort_ThrowInvalidPasswordException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@s1";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePassword(oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordIsTooLong_ThrowInvalidPasswordException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "P@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLongP@s1TooLong";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePassword(oldPassword, newPassword);
	}

	@Test(expected = InvalidPasswordException.class)
	public void changePassword_NewPasswordIsEmpty_ThrowInvalidPasswordException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser8";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePassword(oldPassword, newPassword);
	}

	@Test
	public void resetPassword_PasswordQuestionAnswerIsCorrect_PasswordChanged() {
		final String userName = "sampleUser9";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Nondo Lydienne";

		userService.resetPassword(userName, newPassword, question, answer);

		// Now, we verify that the user can authenticate with the new password
		SecurityContextHolder.clearContext();
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				newPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		authenticationManager.authenticate(authentication);
	}

	@Test(expected = WrongPasswordQuestionAnswerException.class)
	public void resetPassword_PasswordQuestionAnswerIsWrong_ThrowWrongPasswordQuestionAnswerException() {
		final String userName = "sampleUser10";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Wrong Password Answer";

		try {
			userService.resetPassword(userName, newPassword, question, answer);
		} catch (WrongPasswordQuestionAnswerException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(testApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void resetPassword_UserIsLockedOut_ThrowLockedException() {
		final String userName = "sampleUser11";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Nondo Lydienne";

		userService.resetPassword(userName, newPassword, question, answer);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void resetPassword_UserDoesNotExist_ThrowUsernameNotFoundException() {
		final String userName = "missingUser";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Nondo Lydienne";

		try {
			userService.resetPassword(userName, newPassword, question, answer);
		} catch (MembershipNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(testApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void resetPassword_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser20";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Nondo Lydienne";

		try {
			userServiceWithMissingApplication.resetPassword(userName, newPassword, question, answer);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void resetPassword_WrongPasswordQuestionAnswerIsAttemptedMultipleTimes_ThrowLockedException() {
		final String userName = "sampleUser12";
		final String newPassword = "@Ne39assmrd";
		final String question = "Mother name";
		final String answer = "Wrong Password Answer";

		for (int i = 0; i < 6; i++) {
			try {
				userService.resetPassword(userName, newPassword, question, answer);
			} catch (WrongPasswordQuestionAnswerException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			}
		}
	}

	@Test
	public void changePasswordQuestionAndAnswer_CredentialsAreCorrect_PasswordQuestionAndAnswerChanged()
			throws ApplicationNotFoundException, WrongPasswordQuestionAnswerException, UserNotFoundNameException {
		final String userName = "sampleUser14";
		final String password = "P@$$w0rd";
		final String newQuestion = "New Password Question";
		final String newAnswer = "New Password Answer";
		final String newPassword = "@New9assm0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// First, we change the security question and answer.
		userService.changePasswordQuestionAndAnswer(password, newQuestion, newAnswer);
		// Next we want to make sure that the user's password can be reset using
		// the new question and answer.
		userService.resetPassword(userName, newPassword, newQuestion, newAnswer);

		SecurityContextHolder.clearContext();
		authentication = new UsernamePasswordAuthenticationToken(userName, newPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		authenticationManager.authenticate(authentication);
	}

	@Test(expected = AuthenticationException.class)
	public void changePasswordQuestionAndAnswer_PasswordIsWrong_ThrowAuthenticationException()
			throws ApplicationNotFoundException {
		final String userName = "sampleUser15";
		final String password = "Wrong Password";
		final String newPasswordQuestion = "New Password Question";
		final String newPasswordQuestionAnswer = "New Password Answer";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePasswordQuestionAndAnswer(password, newPasswordQuestion, newPasswordQuestionAnswer);
	}

	@Test(expected = MembershipNotFoundException.class)
	public void changePasswordQuestionAndAnswer_UserDoesNotExist_ThrowMembershipNotFoundException()
			throws ApplicationNotFoundException {
		final String userName = "missingUser";
		final String password = "P@$$w0rd";
		final String newPasswordQuestion = "New Password Question";
		final String newPasswordQuestionAnswer = "New Password Answer";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		try {
			userService.changePasswordQuestionAndAnswer(password, newPasswordQuestion, newPasswordQuestionAnswer);
		} catch (MembershipNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(testApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void changePasswordQuestionAndAnswer_ApplicationDoesNotExist_ThrowApplicationNotFoundException()
			throws ApplicationNotFoundException {
		final String userName = "sampleUser16";
		final String password = "P@$$w0rd";
		final String newPasswordQuestion = "New Password Question";
		final String newPasswordQuestionAnswer = "New Password Answer";
		final String missingApplicationName = "Missing Application";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		try {
			userServiceWithMissingApplication.changePasswordQuestionAndAnswer(password, newPasswordQuestion,
					newPasswordQuestionAnswer);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void changePasswordQuestionAndAnswer_UserIsLockedOut_ThrowLockedException()
			throws ApplicationNotFoundException {
		final String userName = "sampleUser17";
		final String password = "P@$$w0rd";
		final String newPasswordQuestion = "New Password Question";
		final String newPasswordQuestionAnswer = "New Password Answer";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePasswordQuestionAndAnswer(password, newPasswordQuestion, newPasswordQuestionAnswer);
	}

	@Test(expected = DisabledException.class)
	public void changePasswordQuestionAndAnswer_UserIsDisabled_ThrowDisabledException()
			throws ApplicationNotFoundException {
		final String userName = "sampleUser18";
		final String password = "P@$$w0rd";
		final String newPasswordQuestion = "New Password Question";
		final String newPasswordQuestionAnswer = "New Password Answer";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService.changePasswordQuestionAndAnswer(password, newPasswordQuestion, newPasswordQuestionAnswer);
	}

	@Test
	public void unlock_UserIsLockedOut_UserUnlocked() {
		final String userName = "sampleUser19";
		final String password = "P@$$w0rd";
		final String question = "Mother name";
		final String answer = "Nondo Lydienne";
		boolean wasLockedOut = false;

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		try {
			authenticationManager.authenticate(authentication);
		} catch (LockedException e) {
			wasLockedOut = true;
		}

		Assert.assertTrue(wasLockedOut);

		userService.unlock(userName, question, answer);

		authenticationManager.authenticate(authentication);
	}

	@Test(expected = UsernameNotFoundException.class)
	public void unlock_UserDoesNotExist_ThrowUsernameNotFoundException() {
		final String userName = "missingUser";
		final String question = "Mother name";
		final String answer = "Nondo Lydienne";
		try {
			userService.unlock(userName, question, answer);
		} catch (MembershipNotFoundException e) {
			Assert.assertEquals(userName, e.getUserName());
			Assert.assertEquals(testApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void unlock_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser21";
		final String missingApplicationName = "Missing Application";
		final String question = "Mother name";
		final String answer = "Nondo Lydienne";

		try {
			userServiceWithMissingApplication.unlock(userName, question, answer);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test
	public void delete_UserExists_UserDeleted() throws UserNotFoundException, ApplicationNotFoundException {
		final String userName = "sampleUser22";
		EntityManagerHelper helper = new EntityManagerHelper(em);

		Map<String, Object> pathValuePairs = new HashMap<>();
		pathValuePairs.put("userName", userName);
		pathValuePairs.put("application.name", testApplicationName);

		User user1 = helper.find(User.class, pathValuePairs);

		userService.delete(userName);

		User user2 = helper.find(User.class, pathValuePairs);

		Assert.assertNotNull(user1);
		Assert.assertNull(user2);
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void delete_ApplicationDoesNotExist_ThrowApplicationNotFoundException()
			throws UserNotFoundException, ApplicationNotFoundException {
		final String userName = "sampleUser23";
		final String missingApplicationName = "Missing Application";
		try {
			userServiceWithMissingApplication.delete(userName);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = UsernameNotFoundException.class)
	public void delete_UserDoesNotExist_ThrowUsernameNotFoundException()
			throws ApplicationNotFoundException, UserNotFoundNameException {
		final String userName = "MissingUser";

		userService.delete(userName);
	}

	@Test
	public void passwordExpired_NewUserHasNotResetPassword_ReturnTrue()
			throws UserNotFoundNameException, ApplicationNotFoundException {
		final String userName = "sampleUser24";

		Boolean passwordExpired = userService.passwordExpired(userName);
		Assert.assertTrue(passwordExpired);
	}

	@Test
	public void passwordExpired_NewUserHasResetPassword_ReturnFalse()
			throws UserNotFoundException, ApplicationNotFoundException {
		final String userName = "sampleUser25";

		Boolean passwordExpired = userService.passwordExpired(userName);
		Assert.assertFalse(passwordExpired);
	}

	@Test(expected = MinimumPasswordAgeViolationException.class)
	public void changePassword_UserIsNewAndMinimumPasswordAgeIsViolated_ThrowMinimumPasswordAgeViolationException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser26";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "C0mpo$itePassword";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService1.changePassword(oldPassword, newPassword);
	}

	@Test(expected = MinimumPasswordAgeViolationException.class)
	public void changePassword_UserIsOldAndMinimumPasswordAgeIsViolated_ThrowMinimumPasswordAgeViolationException()
			throws InvalidPasswordException, PasswordHistoryException, UserNotFoundException,
			MinimumPasswordAgeViolationException, ApplicationNotFoundException {
		final String userName = "sampleUser27";
		final String oldPassword = "P@$$w0rd";
		final String newPassword = "C0mpo$itePassword";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				oldPassword);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		userService1.changePassword(oldPassword, newPassword);
	}

}