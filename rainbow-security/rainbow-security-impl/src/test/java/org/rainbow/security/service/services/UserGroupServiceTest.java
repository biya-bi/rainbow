package org.rainbow.security.service.services;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.rainbow.security.service.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.exceptions.UserNotInGroupException;
import org.rainbow.security.service.services.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/UserGroupServiceTestSetup.sql")
public class UserGroupServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("userGroupService")
	private UserGroupService userGroupService;

	@Autowired
	@Qualifier("userGroupServiceWithMissingApplication")
	private UserGroupService userGroupServiceWithMissingApplication;

	@PersistenceContext
	private EntityManager em;

	private final String missingApplication = "Missing Application";

	@Test
	public void addUsersToGroups_UsersNotInGroups_UsersAddedToGroups() {

		final Long sampleGroup1Id = 6001L;
		final Long sampleGroup2Id = 6002L;

		int noOfUsersInGroup1Before = em.getReference(Group.class, sampleGroup1Id).getUsers().size();
		int noOfUsersInGroup2Before = em.getReference(Group.class, sampleGroup2Id).getUsers().size();

		userGroupService.addUsersToGroups(Arrays.asList("sampleUser1", "sampleUser2"),
				Arrays.asList("Sample Group 1", "Sample Group 2"));

		int noOfUsersInGroup1After = em.getReference(Group.class, sampleGroup1Id).getUsers().size();
		int noOfUsersInGroup2After = em.getReference(Group.class, sampleGroup2Id).getUsers().size();

		Assert.assertTrue(noOfUsersInGroup1Before == 0);
		Assert.assertTrue(noOfUsersInGroup2Before == 1);
		Assert.assertTrue(noOfUsersInGroup1After == 2);
		Assert.assertTrue(noOfUsersInGroup2After == 3); // Sample Group 2
														// already
														// contained sampleUser3
														// before adding the
														// users to the groups.
	}

	@Test(expected = UserAlreadyInGroupException.class)
	public void addUsersToGroups_UserAlreadyInGroup_ThrowUserAlreadyInGroupException() {
		final String sampleUser3 = "sampleUser3";

		final String sampleGroup2 = "Sample Group 2";

		try {
			userGroupService.addUsersToGroups(Arrays.asList(sampleUser3), Arrays.asList(sampleGroup2));
		} catch (UserAlreadyInGroupException e) {
			Assert.assertEquals(sampleUser3, e.getUserName());
			Assert.assertEquals(sampleGroup2, e.getGroupName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void addUsersToGroups_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		try {
			userGroupServiceWithMissingApplication.addUsersToGroups(Arrays.asList("sampleUser6"),
					Arrays.asList("Sample Group 1", "Sample Group 2"));
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = UserNotFoundException.class)
	public void addUsersToGroups_UserDoesNotExist_ThrowUserNotFoundException() {
		final String missingUser = "missingUser";

		try {
			userGroupService.addUsersToGroups(Arrays.asList(missingUser),
					Arrays.asList("Sample Group 1", "Sample Group 2"));
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test(expected = GroupNotFoundException.class)
	public void addUsersToGroups_GroupDoesNotExist_ThrowGroupNotFoundException() {

		final String missingGroup = "Missing Group";

		try {
			userGroupService.addUsersToGroups(Arrays.asList("sampleUser7"),
					Arrays.asList("Sample Group 1", missingGroup));

		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroup, e.getGroupName());
			throw e;
		}
	}

	@Test
	public void removeUsersFromGroups_UsersInGroups_UsersRemoveFromGroups() {

		final Long sampleGroup3Id = 6003L;
		final Long sampleGroup4Id = 6004L;

		int noOfUsersInGroup3Before = em.getReference(Group.class, sampleGroup3Id).getUsers().size();
		int noOfUsersInGroup4Before = em.getReference(Group.class, sampleGroup4Id).getUsers().size();

		em.clear();

		userGroupService.removeUsersFromGroups(Arrays.asList("sampleUser8", "sampleUser9"),
				Arrays.asList("Sample Group 3", "Sample Group 4"));

		int noOfUsersInGroup3After = em.getReference(Group.class, sampleGroup3Id).getUsers().size();
		int noOfUsersInGroup4After = em.getReference(Group.class, sampleGroup4Id).getUsers().size();

		Assert.assertTrue(noOfUsersInGroup3Before == 3);
		Assert.assertTrue(noOfUsersInGroup4Before == 2);
		Assert.assertTrue(noOfUsersInGroup3After == 1);
		Assert.assertTrue(noOfUsersInGroup4After == 0);
	}

	@Test(expected = UserNotInGroupException.class)
	public void removeUsersFromGroups_UserNotInGroup_ThrowUserNotInGroupException() {
		final String sampleUser13 = "sampleUser13";

		final String sampleGroup6 = "Sample Group 6";

		try {
			userGroupService.removeUsersFromGroups(Arrays.asList(sampleUser13),
					Arrays.asList("Sample Group 5", sampleGroup6));

		} catch (UserNotInGroupException e) {
			Assert.assertEquals(sampleUser13, e.getUserName());
			Assert.assertEquals(sampleGroup6, e.getGroupName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void removeUsersFromGroups_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		try {
			userGroupServiceWithMissingApplication.removeUsersFromGroups(Arrays.asList("sampleUser8", "sampleUser9"),
					Arrays.asList("Sample Group 3", "Sample Group 4"));
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = GroupNotFoundException.class)
	public void removeUsersFromGroups_GroupDoesNotExist_ThrowGroupNotFoundException() {

		final String missingGroup = "Missing Group";

		try {
			userGroupService.removeUsersFromGroups(Arrays.asList("sampleUser3"),
					Arrays.asList("Sample Group 2", missingGroup));
		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroup, e.getGroupName());
			throw e;
		}
	}

	@Test(expected = UserNotFoundException.class)
	public void removeUsersFromGroups_UserDoesNotExist_ThrowUserNotFoundException() {
		final String missingUser = "missingUser";

		try {
			userGroupService.removeUsersFromGroups(Arrays.asList(missingUser),
					Arrays.asList("Sample Group 1", "Sample Group 2"));
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test
	public void getGroups_UserIsInGroups_ReturnUserGroups() {
		List<String> groups = userGroupService.getGroups("sampleUser14");
		Assert.assertEquals(2, groups.size());
		Assert.assertTrue(groups.contains("Sample Group 5"));
		Assert.assertTrue(groups.contains("Sample Group 6"));
	}

	@Test(expected = UserNotFoundException.class)
	public void getGroups_UserDoesNotExist_ThrowUUserNotFoundException() {
		String missingUser = "MissingUser";
		try {
			userGroupService.getGroups(missingUser);
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void getGroups_ApplicationDoesNotExist_ThrowApplicationNotFoundException() {
		String missingApplicationName = "Missing Application";
		try {
			userGroupServiceWithMissingApplication.getGroups("sampleUser14");
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

}
