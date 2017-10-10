package org.rainbow.security.service.services;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToUserException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToUserException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/UserAuthorityServiceTestSetup.sql")
public class UserAuthorityServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("userAuthorityService")
	private UserAuthorityService userAuthorityService;
	@Autowired
	@Qualifier("userAuthorityServiceWithMissingApplication")
	private UserAuthorityService userAuthorityServiceWithMissingApplication;

	@PersistenceContext
	private EntityManager em;

	private final String missingApplication = "Missing Application";

	@Test
	public void grantAuthoritiesToUsers_AuthoritiesNotGrantedToUsers_AuthoritiesGrantedToUsers() {

		final Long sampleUser1Id = 10001L;
		final Long sampleUser2Id = 10002L;

		int noOfAuthoritiesForUser1Before = em.getReference(User.class, sampleUser1Id).getAuthorities().size();
		int noOfAuthoritiesForUser2Before = em.getReference(User.class, sampleUser2Id).getAuthorities().size();

		em.clear();

		userAuthorityService.grantAuthoritiesToUsers(Arrays.asList("Authority 10001", "Authority 10002"),
				Arrays.asList("sampleUser1", "sampleUser2"));

		int noOfAuthoritiesForUser1After = em.getReference(User.class, sampleUser1Id).getAuthorities().size();
		int noOfAuthoritiesForUser2After = em.getReference(User.class, sampleUser2Id).getAuthorities().size();

		Assert.assertTrue(noOfAuthoritiesForUser1Before == 0);
		Assert.assertTrue(noOfAuthoritiesForUser2Before == 1);
		Assert.assertTrue(noOfAuthoritiesForUser1After == 2);
		Assert.assertTrue(noOfAuthoritiesForUser2After == 3);
	}

	@Test(expected = AuthorityAlreadyGrantedToUserException.class)
	public void grantAuthoritiesToUsers_AuthorityAlreadyGrantedToUser_ThrowAuthorityAlreadyGrantedToUserException() {
		final String authority3 = "Authority 10003";
		final String sampleUser2 = "sampleUser2";

		try {

			userAuthorityService.grantAuthoritiesToUsers(Arrays.asList(authority3),
					Arrays.asList("sampleUser1", sampleUser2));

		} catch (AuthorityAlreadyGrantedToUserException e) {
			Assert.assertEquals(authority3, e.getAuthorityName());
			Assert.assertEquals(sampleUser2, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void grantAuthoritiesToUsers_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {

		try {
			userAuthorityServiceWithMissingApplication.grantAuthoritiesToUsers(Arrays.asList("Authority 10004"),
					Arrays.asList("sampleUser1", "sampleUser2"));
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = AuthorityNotFoundException.class)
	public void grantAuthoritiesToUsers_AuthorityDoesNotExist_ThrowAuthorityNotFoundException() {

		final String missingAuthority = "Missing Authority";

		try {
			userAuthorityService.grantAuthoritiesToUsers(Arrays.asList(missingAuthority),
					Arrays.asList("sampleUser1", "sampleUser2"));
		} catch (AuthorityNotFoundException e) {
			Assert.assertEquals(missingAuthority, e.getAuthorityName());
			throw e;
		}
	}

	@Test(expected = UserNotFoundException.class)
	public void grantAuthoritiesToUsers_UserDoesNotExist_ThrowUserNotFoundException() {
		final String missingUser = "missingUser";

		try {
			userAuthorityService.grantAuthoritiesToUsers(Arrays.asList("Authority 10005"),
					Arrays.asList("sampleUser1", missingUser));
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test
	public void revokeAuthoritiesFromUsers_AuthoritiesGrantedUsers_AuthoritiesRevokedFromUsers() {

		final Long sampleUser3Id = 10003L;
		final Long sampleUser4Id = 10004L;

		int noOfAuthoritiesForUser3Before = em.getReference(User.class, sampleUser3Id).getAuthorities().size();
		int noOAuthoritiesForUser4Before = em.getReference(User.class, sampleUser4Id).getAuthorities().size();

		userAuthorityService.revokeAuthoritiesFromUsers(Arrays.asList("Authority 10006", "Authority 10007"),
				Arrays.asList("sampleUser3", "sampleUser4"));

		int noOfAuthoritiesForUser3After = em.getReference(User.class, sampleUser3Id).getAuthorities().size();
		int noOfAuthoritiesForUser4After = em.getReference(User.class, sampleUser4Id).getAuthorities().size();

		Assert.assertTrue(noOfAuthoritiesForUser3Before == 3);
		Assert.assertTrue(noOAuthoritiesForUser4Before == 2);
		Assert.assertTrue(noOfAuthoritiesForUser3After == 1);
		Assert.assertTrue(noOfAuthoritiesForUser4After == 0);
	}

	@Test(expected = AuthorityNotGrantedToUserException.class)
	public void revokeAuthoritiesFromUsers_AuthoritiesGrantedUsers_ThrowAuthorityNotGrantedToUserException() {

		final String sampleUser4 = "sampleUser4";

		final String authority8 = "Authority 10008";

		try {
			userAuthorityService.revokeAuthoritiesFromUsers(Arrays.asList(authority8),
					Arrays.asList("sampleUser3", sampleUser4));

		} catch (AuthorityNotGrantedToUserException e) {
			Assert.assertEquals(authority8, e.getAuthorityName());
			Assert.assertEquals(sampleUser4, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void revokeAuthoritiesFromUsers_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {

		try {
			userAuthorityServiceWithMissingApplication.revokeAuthoritiesFromUsers(
					Arrays.asList("Authority 10006", "Authority 10007"), Arrays.asList("sampleUser3", "sampleUser4"));
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}

	}

	@Test(expected = UserNotFoundException.class)
	public void revokeAuthoritiesFromUsers_UserDoesNotExist_ThrowUserNotFoundException() {

		final String missingUser = "missingUser";

		try {
			userAuthorityService.revokeAuthoritiesFromUsers(Arrays.asList("Authority 10009"),
					Arrays.asList("sampleUser5", missingUser));
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test(expected = AuthorityNotFoundException.class)
	public void revokeAuthoritiesFromUsers_AuthorityDoesNotExist_ThrowAuthorityNotFoundException() {

		final String missingAuthority = "Missing Authority";

		try {
			userAuthorityService.revokeAuthoritiesFromUsers(Arrays.asList(missingAuthority),
					Arrays.asList("sampleUser3", "sampleUser4"));
		} catch (AuthorityNotFoundException e) {
			Assert.assertEquals(missingAuthority, e.getAuthorityName());
			throw e;
		}
	}

	@Test
	public void getAuthorities_AuthoritiesGrantedToUser_ReturnAuthorities() {
		// Two authorities are granted to the user's group, and one authority is
		// granted directly to the user.
		List<String> authorities = userAuthorityService.getAuthorities("sampleUser7");
		Assert.assertEquals(3, authorities.size());
		Assert.assertTrue(authorities.contains("Authority 10001"));
		Assert.assertTrue(authorities.contains("Authority 10002"));
		Assert.assertTrue(authorities.contains("Authority 10003"));
	}

	@Test(expected = UserNotFoundException.class)
	public void getAuthorities_UserDoesNotExist_ThrowUserNotFoundException() {
		String missingUser = "missingUser";
		try {
			userAuthorityService.getAuthorities(missingUser);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void getAuthorities_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		try {
			userAuthorityServiceWithMissingApplication.getAuthorities("sampleUser7");
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}
}
