package org.rainbow.security.service.services;

import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityAlreadyGrantedToGroupException;
import org.rainbow.security.service.exceptions.AuthorityNotFoundException;
import org.rainbow.security.service.exceptions.AuthorityNotGrantedToGroupException;
import org.rainbow.security.service.exceptions.GroupNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/GroupAuthorityServiceTestSetup.sql")
public class GroupAuthorityServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("groupAuthorityService")
	private GroupAuthorityService groupAuthorityService;
	@Autowired
	@Qualifier("groupAuthorityServiceWithMissingApplication")
	private GroupAuthorityService groupAuthorityServiceWithMissingApplication;

	@PersistenceContext
	private EntityManager em;

	private final String missingApplication = "Missing Application";

	@Test
	public void grantAuthoritiesToGroups_AuthoritiesNotGrantedToGroups_AuthoritiesGrantedToGroups()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityAlreadyGrantedToGroupException {

		final Long sampleGroup1Id = 3001L;
		final Long sampleGroup2Id = 3002L;

		int noOfAuthoritiesForGroup1Before = em.getReference(Group.class, sampleGroup1Id).getAuthorities().size();
		int noOfAuthoritiesForGroup2Before = em.getReference(Group.class, sampleGroup2Id).getAuthorities().size();

		em.clear();

		groupAuthorityService.grantAuthoritiesToGroups(Arrays.asList("Authority 3001", "Authority 3002"),
				Arrays.asList("Sample Group 3001", "Sample Group 3002"));

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
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException {
		final String authority3 = "Authority 3003";
		final String sampleGroup2 = "Sample Group 3002";

		try {

			groupAuthorityService.grantAuthoritiesToGroups(Arrays.asList(authority3),
					Arrays.asList("Sample Group 3001", sampleGroup2));

		} catch (AuthorityAlreadyGrantedToGroupException e) {
			Assert.assertEquals(authority3, e.getAuthorityName());
			Assert.assertEquals(sampleGroup2, e.getGroupName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void grantAuthoritiesToGroups_ApplicationDoesNotExist_ThrowApplicationNotFoundException()
			throws AuthorityNotFoundException, GroupNotFoundException, AuthorityAlreadyGrantedToGroupException {

		try {
			groupAuthorityServiceWithMissingApplication.grantAuthoritiesToGroups(Arrays.asList("Authority 3004"),
					Arrays.asList("Sample Group 3001", "Sample Group 3002"));
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}
	}

	@Test(expected = AuthorityNotFoundException.class)
	public void grantAuthoritiesToGroups_AuthorityDoesNotExist_ThrowAuthorityNotFoundException()
			throws GroupNotFoundException, AuthorityAlreadyGrantedToGroupException, AuthorityNotFoundException {

		final String missingAuthority = "Missing Authority";

		try {
			groupAuthorityService.grantAuthoritiesToGroups(Arrays.asList(missingAuthority),
					Arrays.asList("Sample Group 3001", "Sample Group 3002"));
		} catch (AuthorityNotFoundException e) {
			Assert.assertEquals(missingAuthority, e.getAuthorityName());
			throw e;
		}
	}

	@Test(expected = GroupNotFoundException.class)
	public void grantAuthoritiesToGroups_GroupDoesNotExist_ThrowGroupNotFoundException()
			throws AuthorityNotFoundException, AuthorityAlreadyGrantedToGroupException, GroupNotFoundException {
		final String missingGroup = "Missing Group";

		try {
			groupAuthorityService.grantAuthoritiesToGroups(Arrays.asList("Authority 3005"),
					Arrays.asList("Sample Group 3001", missingGroup));
		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroup, e.getGroupName());
			throw e;
		}
	}

	@Test
	public void revokeAuthoritiesFromGroups_AuthoritiesGrantedGroups_AuthoritiesRevokedFromGroups()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException {

		final Long sampleGroup3Id = 3003L;
		final Long sampleGroup4Id = 3004L;

		int noOfAuthoritiesForGroup3Before = em.getReference(Group.class, sampleGroup3Id).getAuthorities().size();
		int noOAuthoritiesForGroup4Before = em.getReference(Group.class, sampleGroup4Id).getAuthorities().size();

		groupAuthorityService.revokeAuthoritiesFromGroups(Arrays.asList("Authority 3006", "Authority 3007"),
				Arrays.asList("Sample Group 3003", "Sample Group 3004"));

		int noOfAuthoritiesForGroup3After = em.getReference(Group.class, sampleGroup3Id).getAuthorities().size();
		int noOfAuthoritiesForGroup4After = em.getReference(Group.class, sampleGroup4Id).getAuthorities().size();

		Assert.assertTrue(noOfAuthoritiesForGroup3Before == 3);
		Assert.assertTrue(noOAuthoritiesForGroup4Before == 2);
		Assert.assertTrue(noOfAuthoritiesForGroup3After == 1);
		Assert.assertTrue(noOfAuthoritiesForGroup4After == 0);
	}

	@Test(expected = AuthorityNotGrantedToGroupException.class)
	public void revokeAuthoritiesFromGroups_AuthoritiesGrantedGroups_ThrowAuthorityNotGrantedToGroupException()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException {

		final String sampleGroup4 = "Sample Group 3004";

		final String authority8 = "Authority 3008";

		try {
			groupAuthorityService.revokeAuthoritiesFromGroups(Arrays.asList(authority8),
					Arrays.asList("Sample Group 3003", sampleGroup4));

		} catch (AuthorityNotGrantedToGroupException e) {
			Assert.assertEquals(authority8, e.getAuthorityName());
			Assert.assertEquals(sampleGroup4, e.getGroupName());
			throw e;
		}
	}

	@Test(expected = ApplicationNotFoundException.class)
	public void revokeAuthoritiesFromGroups_ApplicationDoesNotExist_ThrowApplicationNotFoundException()
			throws GroupNotFoundException, AuthorityNotFoundException, AuthorityNotGrantedToGroupException {

		try {
			groupAuthorityServiceWithMissingApplication.revokeAuthoritiesFromGroups(
					Arrays.asList("Authority 3006", "Authority 3007"),
					Arrays.asList("Sample Group 3003", "Sample Group 3004"));
		} catch (ApplicationNotFoundException e) {
			Assert.assertEquals(missingApplication, e.getApplicationName());
			throw e;
		}

	}

	@Test(expected = GroupNotFoundException.class)
	public void revokeAuthoritiesFromGroups_GroupDoesNotExist_ThrowGroupNotFoundException()
			throws AuthorityNotFoundException, AuthorityNotGrantedToGroupException, GroupNotFoundException {

		final String missingGroup = "Missing Group";

		try {
			groupAuthorityService.revokeAuthoritiesFromGroups(Arrays.asList("Authority 3009"),
					Arrays.asList("Sample Group 3005", missingGroup));
		} catch (GroupNotFoundException e) {
			Assert.assertEquals(missingGroup, e.getGroupName());
			throw e;
		}
	}

	@Test(expected = AuthorityNotFoundException.class)
	public void revokeAuthoritiesFromGroups_AuthorityDoesNotExist_ThrowAuthorityNotFoundException()
			throws GroupNotFoundException, AuthorityNotGrantedToGroupException, AuthorityNotFoundException {

		final String missingAuthority = "Missing Authority";

		try {
			groupAuthorityService.revokeAuthoritiesFromGroups(Arrays.asList(missingAuthority),
					Arrays.asList("Sample Group 3003", "Sample Group 3004"));
		} catch (AuthorityNotFoundException e) {
			Assert.assertEquals(missingAuthority, e.getAuthorityName());
			throw e;
		}
	}
}
