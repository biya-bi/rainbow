package org.rainbow.security.service.services;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.CredentialsNotFoundException;
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
@DatabaseInitialize("src/test/resources/SQL/UserDetailsAuthenticationProviderTestSetup.sql")
public class UserDetailsAuthenticationProviderTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("authenticationManager1")
	private AuthenticationManager authenticationManager1;

	@Autowired
	@Qualifier("authenticationManagerWithMissingApplication")
	private AuthenticationManager authenticationManagerWithMissingApplication;

	@Autowired
	@Qualifier("authenticationManager2")
	private AuthenticationManager authenticationManager2;

	private final String missingApplicationName = "Missing Application";

	@Test
	public void authenticate_CredentialsAreValid_UserAuthenticated() {
		final String userName = "sampleUser1";
		final String password = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		// If no exception is thrown, then the user is successfully
		// authenticated.
		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = BadCredentialsException.class)
	public void authenticate_PasswordIsWrong_ThrowBadCredentialsException() {
		final String userName = "sampleUser2";
		final String password = "Wrong password";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = CredentialsNotFoundException.class)
	public void authenticate_UserDoesNotExist_ThrowCredentialsNotFoundException() {
		final String userName = "Missing User";
		final String password = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void authenticate_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		final String userName = "sampleUser3";
		final String password = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		try {
			authenticationManagerWithMissingApplication.authenticate(authentication);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = LockedException.class)
	public void authenticate_WrongPasswordAttemptedMultipleTimes_ThrowLockedException() {
		final String userName = "sampleUser4";
		final String password = "Wrong password";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		for (int i = 0; i < 6; i++) {
			try {
				authenticationManager1.authenticate(authentication);
			} catch (BadCredentialsException e) {
				// This is a intentional dummy catch block. We know that the
				// BadCredentialsException exception will be thrown for i less
				// than the maximum allowed failed password attempts.
			}
		}
	}

	@Test(expected = DisabledException.class)
	public void authenticate_UserIsLocked_ThrowDisabledException() {
		final String userName = "sampleUser5";
		final String password = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		authenticationManager1.authenticate(authentication);
	}

	@Test(expected = CredentialsExpiredException.class)
	public void authenticate_NewUserHasNotResetPassword_ThrowCredentialExpiredException() {
		final String userName = "sampleUser6";
		final String password = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		authenticationManager1.authenticate(authentication);

	}

	/**
	 * In this test, we want to show that a new user who has changed his/her
	 * password is successfully authenticated. For this, we use a user who was
	 * created two days ago and whose password was changed one day ago. Since
	 * the minimum password age of the password policy is 0, the user must
	 * change his/her password at next logon. We therefore configure sampleUser7
	 * to reflect a user who has logged in one day ago and changed his/her
	 * password.
	 */
	@Test
	public void authenticate_NewUserHasChangedPassword_UserAuthenticated() {
		final String userName = "sampleUser7";
		final String password = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		authenticationManager1.authenticate(authentication);
	}

	@Test
	public void authenticate_MaximumPasswordAgeIsZeroAndNewUserHasNotResetPassword_CredentialsNotExpired() {
		final String userName = "sampleUser8";
		final String password = "P@$$w0rd";

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userName,
				password);
		authenticationManager2.authenticate(authentication);

	}
}