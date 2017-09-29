package org.rainbow.security.service.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.DuplicateUserException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 
 * @author Biya-Bi
 *
 */
@DatabaseInitialize("src/test/resources/SQL/UserServiceTestSetup.sql")
public class UserServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("userService")
	private Service<User, Long, SearchOptions> userService;

	@PersistenceContext
	private EntityManager em;

	private final String testApplicationName = "Test Application";

	@Test
	public void create_UserIsValid_UserCreated() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "newUser";
		final String password = "P@$$w0rd";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
			expected.setMembership(expectedMembership);

		userService.create(expected);

		User actual = em.getReference(User.class, expected.getId());
		Assert.assertNotNull(actual);
		Assert.assertEquals(expected.getUserName(), actual.getUserName());
		Membership actualMembership = actual.getMembership();
		Assert.assertNotNull(actualMembership);
		Assert.assertEquals(expectedMembership.isEnabled(), actualMembership.isEnabled());
		Assert.assertEquals(expectedMembership.isLocked(), actualMembership.isLocked());
	}

	@Test(expected = DuplicateUserException.class)
	public void create_UserAlreadyExist_ThrowDuplicateUserException() throws Exception {
		Application application = new Application(8001L, testApplicationName);

		final String userName = "sampleUser1";
		final String password = "P@$$w0rd";

		User expected = new User(userName, application);
		Membership expectedMembership = new Membership(expected, password, "Description", true, false);
		expected.setMembership(expectedMembership);

		try {
			userService.create(expected);
		} catch (DuplicateUserException e) {
			Assert.assertEquals(userName, e.getUserName());
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
		expected.setMembership(expectedMembership);

		userService.create(expected);
	}

}