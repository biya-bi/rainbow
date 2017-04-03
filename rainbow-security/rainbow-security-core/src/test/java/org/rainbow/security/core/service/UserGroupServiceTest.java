/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.persistence.exceptions.ApplicationNotFoundException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserAlreadyInGroupException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundException;
import org.rainbow.security.core.persistence.exceptions.UserNotFoundNameException;
import org.rainbow.security.core.persistence.exceptions.UserNotInGroupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 * @author Biya-Bi
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class UserGroupServiceTest {

	@Autowired
	@Qualifier("userGroupService")
	private UserGroupService userGroupService;

	@PersistenceContext
	private EntityManager em;

	private static final MySqlDatabase DATABASE = new MySqlDatabase();

	private final Application application = new Application(6001L, "Test Application");

	@BeforeClass
	public static void setUpClass() throws SQLException, IOException {
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/UserGroupServiceTestSetup.sql");
	}

	@AfterClass
	public static void cleanUpClass() throws SQLException, IOException {
		DATABASE.execute("src/test/resources/Cleanup.sql");
	}

	@After
	public void tearDown() {
		em.clear();
	}

	@Test
	public void addUsersToGroups_UsersNotInGroups_UsersAddedToGroups() throws UserNotFoundException,
			GroupNotFoundException, UserAlreadyInGroupException, NonexistentEntityException {
		final Long sampleUser1Id = 6001L;
		final Long sampleUser2Id = 6002L;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(sampleUser1Id);
		userIds.add(sampleUser2Id);

		final Long sampleGroup1Id = 6001L;
		final Long sampleGroup2Id = 6002L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(sampleGroup2Id);

		int noOfUsersInGroup1Before = em.getReference(Group.class, sampleGroup1Id).getUsers().size();
		int noOfUsersInGroup2Before = em.getReference(Group.class, sampleGroup2Id).getUsers().size();

		userGroupService.addUsersToGroups(userIds, groupIds, application.getId());

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
	public void addUsersToGroups_UserAlreadyInGroup_ThrowUserAlreadyInGroupException() throws UserNotFoundException,
			GroupNotFoundException, UserAlreadyInGroupException, NonexistentEntityException {
		final Long sampleUser3Id = 6003L;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(sampleUser3Id);

		final Long sampleGroup2Id = 6002L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup2Id);

		try {
			userGroupService.addUsersToGroups(userIds, groupIds, application.getId());
		} catch (UserAlreadyInGroupException e) {
			Assert.assertEquals(sampleUser3Id, e.getUserId());
			Assert.assertEquals(sampleGroup2Id, e.getGroupId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void addUsersToGroups_ApplicationDoesNotExist_ThrowNonexistentEntityException() throws UserNotFoundException,
			GroupNotFoundException, UserAlreadyInGroupException, NonexistentEntityException {
		final Long missingApplicationId = Long.MAX_VALUE;

		final Long sampleUser6Id = 6006L;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(sampleUser6Id);

		final Long sampleGroup1Id = 6001L;
		final Long sampleGroup2Id = 6002L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(sampleGroup2Id);

		userGroupService.addUsersToGroups(userIds, groupIds, missingApplicationId);
	}

	@Test(expected = UserNotFoundException.class)
	public void addUsersToGroups_UserDoesNotExist_ThrowUserNotFoundException() throws GroupNotFoundException,
			UserAlreadyInGroupException, UserNotFoundException, NonexistentEntityException {
		final Long missingUserId = Long.MAX_VALUE;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(missingUserId);

		final Long sampleGroup1Id = 6001L;
		final Long sampleGroup2Id = 6002L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(sampleGroup2Id);

		try {
			userGroupService.addUsersToGroups(userIds, groupIds, application.getId());
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUserId, e.getUserId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test(expected = GroupNotFoundException.class)
	public void addUsersToGroups_GroupDoesNotExist_ThrowGroupNotFoundException() throws GroupNotFoundException,
			UserAlreadyInGroupException, UserNotFoundException, NonexistentEntityException {
		final Long sampleUser7Id = 6007L;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(sampleUser7Id);

		final Long sampleGroup1Id = 6001L;
		final Long missingGroupId = Long.MAX_VALUE;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(missingGroupId);

		try {
			userGroupService.addUsersToGroups(userIds, groupIds, application.getId());
		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroupId, e.getGroupId());
			throw e;
		}
	}

	@Test
	public void removeUsersFromGroups_UsersInGroups_UsersRemoveFromGroups()
			throws GroupNotFoundException, UserNotFoundException, UserNotInGroupException, NonexistentEntityException {
		final Long sampleUser8Id = 6008L;
		final Long sampleUser9Id = 6009L;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(sampleUser8Id);
		userIds.add(sampleUser9Id);

		final Long sampleGroup3Id = 6003L;
		final Long sampleGroup4Id = 6004L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup3Id);
		groupIds.add(sampleGroup4Id);

		int noOfUsersInGroup3Before = em.getReference(Group.class, sampleGroup3Id).getUsers().size();
		int noOfUsersInGroup4Before = em.getReference(Group.class, sampleGroup4Id).getUsers().size();

		em.clear();

		userGroupService.removeUsersFromGroups(userIds, groupIds, application.getId());

		int noOfUsersInGroup3After = em.getReference(Group.class, sampleGroup3Id).getUsers().size();
		int noOfUsersInGroup4After = em.getReference(Group.class, sampleGroup4Id).getUsers().size();

		Assert.assertTrue(noOfUsersInGroup3Before == 3);
		Assert.assertTrue(noOfUsersInGroup4Before == 2);
		Assert.assertTrue(noOfUsersInGroup3After == 1);
		Assert.assertTrue(noOfUsersInGroup4After == 0);
	}

	@Test(expected = UserNotInGroupException.class)
	public void removeUsersFromGroups_UserNotInGroup_ThrowUserNotInGroupException()
			throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, NonexistentEntityException {
		final Long sampleUser13Id = 6013L;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(sampleUser13Id);

		final Long sampleGroup5Id = 6005L;
		final Long sampleGroup6Id = 6006L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup5Id);
		groupIds.add(sampleGroup6Id);

		try {
			userGroupService.removeUsersFromGroups(userIds, groupIds, application.getId());
		} catch (UserNotInGroupException e) {
			Assert.assertEquals(sampleUser13Id, e.getUserId());
			Assert.assertEquals(sampleGroup6Id, e.getGroupId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void removeUsersFromGroups_ApplicationDoesNotExist_ThrowNonexistentEntityException()
			throws UserNotFoundException, GroupNotFoundException, UserNotInGroupException, NonexistentEntityException {
		final Long missingApplicationId = Long.MAX_VALUE;

		final Long sampleUser8Id = 6008L;
		final Long sampleUser9Id = 6009L;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(sampleUser8Id);
		userIds.add(sampleUser9Id);

		final Long sampleGroup3Id = 6003L;
		final Long sampleGroup4Id = 6004L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup3Id);
		groupIds.add(sampleGroup4Id);

		userGroupService.removeUsersFromGroups(userIds, groupIds, missingApplicationId);
	}

	@Test(expected = GroupNotFoundException.class)
	public void removeUsersFromGroups_GroupDoesNotExist_ThrowGroupNotFoundException()
			throws UserNotFoundException, UserNotInGroupException, GroupNotFoundException, NonexistentEntityException {
		final Long sampleUser3Id = 6003L;
		final List<Long> usersId = new ArrayList<>();
		usersId.add(sampleUser3Id);

		final Long sampleGroup2Id = 6002L;
		final Long missingGroupId = Long.MAX_VALUE;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup2Id);
		groupIds.add(missingGroupId);

		try {
			userGroupService.removeUsersFromGroups(usersId, groupIds, application.getId());
		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroupId, e.getGroupId());
			throw e;
		}
	}

	@Test(expected = UserNotFoundException.class)
	public void removeUsersFromGroups_UserDoesNotExist_ThrowUserNotFoundException()
			throws GroupNotFoundException, UserNotInGroupException, UserNotFoundException, NonexistentEntityException {
		final Long missingUserId = Long.MAX_VALUE;
		final List<Long> userIds = new ArrayList<>();
		userIds.add(missingUserId);

		final Long sampleGroup1Id = 6001L;
		final Long sampleGroup2Id = 6002L;
		final List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(sampleGroup2Id);

		try {
			userGroupService.removeUsersFromGroups(userIds, groupIds, application.getId());
		} catch (UserNotFoundException e) {
			Assert.assertEquals(missingUserId, e.getUserId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test
	public void getGroups_UserIsInGroups_ReturnUserGroups() throws UserNotFoundException, ApplicationNotFoundException {
		List<String> groups = userGroupService.getGroups("sampleUser14", application.getName());
		Assert.assertEquals(2, groups.size());
		Assert.assertTrue(groups.contains("Sample Group 5"));
		Assert.assertTrue(groups.contains("Sample Group 6"));
	}

	@Test(expected = UserNotFoundNameException.class)
	public void getGroups_UserDoesNotExist_ThrowUUserNotFoundNameException()
			throws UserNotFoundException, ApplicationNotFoundException {
		String missingUser = "MissingUser";
		try {
			userGroupService.getGroups(missingUser, application.getName());
		} catch (UserNotFoundNameException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void getGroups_ApplicationDoesNotExist_ThrowApplicationNotFoundException()
			throws UserNotFoundNameException, ApplicationNotFoundException {
		String missingApplicationName = "Missing Application";
		try {
			userGroupService.getGroups("sampleUser14", missingApplicationName);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}

	@Test
	public void getAuthorities_AuthoritiesGrantedToUserGroups_ReturnAuthoritiesGrantedToUserGroups()
			throws UserNotFoundException, ApplicationNotFoundException {
		List<String> authorities = userGroupService.getAuthorities("sampleUser14", application.getName());
		Assert.assertEquals(2, authorities.size());
		Assert.assertTrue(authorities.contains("Authority 1"));
		Assert.assertTrue(authorities.contains("Authority 2"));
	}

	@Test(expected = UserNotFoundNameException.class)
	public void getAuthorities_UserDoesNotExist_ThrowUserNotFoundNameException()
			throws UserNotFoundException, ApplicationNotFoundException {
		String missingUser = "MissingUser";
		try {
			userGroupService.getAuthorities(missingUser, application.getName());
		} catch (UserNotFoundNameException e) {
			Assert.assertEquals(missingUser, e.getUserName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void getAuthorities_ApplicationDoesNotExist_ThrowApplicationNotFoundException()
			throws UserNotFoundException, ApplicationNotFoundException {
		String missingApplicationName = "Missing Application";
		try {
			userGroupService.getAuthorities("sampleUser14", missingApplicationName);
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplicationName, e.getApplicationName());
			throw e;
		}
	}
}
