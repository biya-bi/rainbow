/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.persistence.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotFoundException;
import org.rainbow.security.core.persistence.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.core.persistence.exceptions.GroupNotFoundException;
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
public class GroupAuthorityServiceTest {

	@Autowired
	@Qualifier("groupAuthorityService")
	private GroupAuthorityService groupAuthorityService;

	@PersistenceContext
	private EntityManager em;

	private final Application application = new Application(3001L, "Test Application");

	private static MySqlDatabase DATABASE;

	@Autowired
	public void initializeDatabase(MySqlDatabase mySqlDatabase)
			throws FileNotFoundException, SQLException, IOException {
		DATABASE = mySqlDatabase;
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/GroupAuthorityServiceTestSetup.sql");
	}

	@AfterClass
	public static void cleanUpClass() throws SQLException, IOException {
		DATABASE.execute("src/test/resources/Cleanup.sql");
	}

	@Before
	public void setUp() {
		em.clear();
	}

	@After
	public void tearDown() {
		em.clear();
	}

	@Test
	public void grantAuthoritiesToGroups_AuthoritiesNotGrantedToGroups_AuthoritiesGrantedToGroups()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityAlreadyGrantedToGroupException,
			NonexistentEntityException {

		final Long sampleGroup1Id = 3001L;
		final Long sampleGroup2Id = 3002L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(sampleGroup2Id);

		final Long sampleAuthority1Id = 3001L;
		final Long sampleAuthority2Id = 3002L;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(sampleAuthority1Id);
		authorityIds.add(sampleAuthority2Id);

		int noOfAuthoritiesForGroup1Before = em.getReference(Group.class, sampleGroup1Id).getAuthorities().size();
		int noOfAuthoritiesForGroup2Before = em.getReference(Group.class, sampleGroup2Id).getAuthorities().size();

		em.clear();

		groupAuthorityService.grantAuthoritiesToGroups(authorityIds, groupIds, application.getId());

		int noOfAuthoritiesForGroup1After = em.getReference(Group.class, sampleGroup1Id).getAuthorities().size();
		int noOfAuthoritiesForGroup2After = em.getReference(Group.class, sampleGroup2Id).getAuthorities().size();

		Assert.assertTrue(noOfAuthoritiesForGroup1Before == 0);
		Assert.assertTrue(noOfAuthoritiesForGroup2Before == 1);
		Assert.assertTrue(noOfAuthoritiesForGroup1After == 2);
		Assert.assertTrue(noOfAuthoritiesForGroup2After == 3); // Sample Group 2
																// already had
																// Authority 3
																// granted
																// before
																// granting the
																// authoritys
																// the roles.
	}

	@Test(expected = AuthorityAlreadyGrantedToGroupException.class)
	public void grantAuthoritiesToGroups_AuthorityAlreadyGrantedToGroup_ThrowAuthorityAlreadyGrantedToGroupException()
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException,
			NonexistentEntityException {

		final Long group2Id = 3002L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(group2Id);

		final Long authority3Id = 3003L;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(authority3Id);

		try {
			groupAuthorityService.grantAuthoritiesToGroups(authorityIds, groupIds, application.getId());
		} catch (AuthorityAlreadyGrantedToGroupException e) {
			Assert.assertEquals(authority3Id, e.getAuthorityId());
			Assert.assertEquals(group2Id, e.getGroupId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void grantAuthoritiesToGroups_ApplicationDoesNotExist_ThrowNonexistentEntityException()
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException,
			NonexistentEntityException {
		final Long missingApplicationId = Long.MAX_VALUE;

		final Long sampleGroup1Id = 3001L;
		final Long sampleGroup2Id = 3002L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(sampleGroup2Id);

		final Long authority4Id = 3004L;

		List<Long> authorities = new ArrayList<>();
		authorities.add(authority4Id);

		groupAuthorityService.grantAuthoritiesToGroups(authorities, groupIds, missingApplicationId);
	}

	@Test(expected = AuthorityNotFoundException.class)
	public void grantAuthoritiesToGroups_AuthorityDoesNotExist_ThrowAuthorityNotFoundException()
			throws GroupNotFoundException, AuthorityAlreadyGrantedToGroupException, AuthorityNotFoundException,
			NonexistentEntityException {
		final Long sampleGroup1Id = 3001L;
		final Long sampleGroup2Id = 3002L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(sampleGroup2Id);

		final Long missingAuthorityId = Long.MAX_VALUE;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(missingAuthorityId);

		try {
			groupAuthorityService.grantAuthoritiesToGroups(authorityIds, groupIds, application.getId());
		} catch (AuthorityNotFoundException e) {
			Assert.assertEquals(missingAuthorityId, e.getAuthorityId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test(expected = GroupNotFoundException.class)
	public void grantAuthoritiesToGroups_GroupDoesNotExist_ThrowGroupNotFoundException()
			throws AuthorityNotFoundException, AuthorityAlreadyGrantedToGroupException, GroupNotFoundException,
			NonexistentEntityException {
		final Long sampleGroup1Id = 3001L;
		final Long missingGroupId = Long.MAX_VALUE;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup1Id);
		groupIds.add(missingGroupId);

		final Long authority5Id = 3005L;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(authority5Id);

		try {
			groupAuthorityService.grantAuthoritiesToGroups(authorityIds, groupIds, application.getId());
		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroupId, e.getGroupId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test
	public void revokeAuthoritiesFromGroups_AuthoritiesGrantedGroups_AuthoritiesRevokedFromGroups()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException,
			NonexistentEntityException {

		final Long sampleGroup3Id = 3003L;
		final Long sampleGroup4Id = 3004L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup3Id);
		groupIds.add(sampleGroup4Id);

		final Long sampleAuthority6Id = 3006L;
		final Long sampleAuthority7Id = 3007L;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(sampleAuthority6Id);
		authorityIds.add(sampleAuthority7Id);

		int noOfAuthoritiesForGroup3Before = em.getReference(Group.class, sampleGroup3Id).getAuthorities().size();
		int noOAuthoritiesForGroup4Before = em.getReference(Group.class, sampleGroup4Id).getAuthorities().size();

		groupAuthorityService.revokeAuthoritiesFromGroups(authorityIds, groupIds, application.getId());

		int noOfAuthoritiesForGroup3After = em.getReference(Group.class, sampleGroup3Id).getAuthorities().size();
		int noOfAuthoritiesForGroup4After = em.getReference(Group.class, sampleGroup4Id).getAuthorities().size();

		Assert.assertTrue(noOfAuthoritiesForGroup3Before == 3);
		Assert.assertTrue(noOAuthoritiesForGroup4Before == 2);
		Assert.assertTrue(noOfAuthoritiesForGroup3After == 1);
		Assert.assertTrue(noOfAuthoritiesForGroup4After == 0);
	}

	@Test(expected = AuthorityNotGrantedToGroupException.class)
	public void revokeAuthoritiesFromGroups_AuthoritiesGrantedGroups_ThrowAuthorityNotGrantedToGroupException()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException,
			NonexistentEntityException {
		final Long sampleGroup3Id = 3003L;
		final Long sampleGroup4Id = 3004L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup3Id);
		groupIds.add(sampleGroup4Id);

		final Long authority8Id = 3008L;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(authority8Id);

		try {
			groupAuthorityService.revokeAuthoritiesFromGroups(authorityIds, groupIds, application.getId());
		} catch (AuthorityNotGrantedToGroupException e) {
			Assert.assertEquals(authority8Id, e.getAuthorityId());
			Assert.assertEquals(sampleGroup4Id, e.getGroupId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void revokeAuthoritiesFromGroups_ApplicationDoesNotExist_ThrowNonexistentEntityException()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException,
			NonexistentEntityException {
		final Long missingApplicationId = Long.MAX_VALUE;

		final Long sampleGroup3Id = 300L;
		final Long sampleGroup4Id = 400L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup3Id);
		groupIds.add(sampleGroup4Id);

		final Long authority6Id = 3006L;
		final Long authority7Id = 3007L;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(authority6Id);
		authorityIds.add(authority7Id);

		groupAuthorityService.revokeAuthoritiesFromGroups(authorityIds, groupIds, missingApplicationId);
	}

	@Test(expected = GroupNotFoundException.class)
	public void revokeAuthoritiesFromGroups_GroupDoesNotExist_ThrowGroupNotFoundException()
			throws AuthorityNotFoundException, AuthorityNotGrantedToGroupException, GroupNotFoundException,
			NonexistentEntityException {
		final Long sampleGroup5Id = 3005L;
		final Long missingGroupId = Long.MAX_VALUE;

		List<Long> groups = new ArrayList<>();
		groups.add(sampleGroup5Id);
		groups.add(missingGroupId);

		final Long authority9Id = 3009L;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(authority9Id);

		try {
			groupAuthorityService.revokeAuthoritiesFromGroups(authorityIds, groups, application.getId());
		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroupId, e.getGroupId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}

	@Test(expected = AuthorityNotFoundException.class)
	public void revokeAuthoritiesFromGroups_AuthorityDoesNotExist_ThrowAuthorityNotFoundException()
			throws GroupNotFoundException, AuthorityNotGrantedToGroupException, AuthorityNotFoundException,
			NonexistentEntityException {
		final Long sampleGroup3Id = 3003L;
		final Long sampleGroup4Id = 3004L;

		List<Long> groupIds = new ArrayList<>();
		groupIds.add(sampleGroup3Id);
		groupIds.add(sampleGroup4Id);

		final Long missingAuthorityId = Long.MAX_VALUE;

		List<Long> authorityIds = new ArrayList<>();
		authorityIds.add(missingAuthorityId);

		try {
			groupAuthorityService.revokeAuthoritiesFromGroups(authorityIds, groupIds, application.getId());
		} catch (AuthorityNotFoundException e) {
			Assert.assertEquals(missingAuthorityId, e.getAuthorityId());
			Assert.assertEquals(application.getId(), e.getApplicationId());
			throw e;
		}
	}
}
