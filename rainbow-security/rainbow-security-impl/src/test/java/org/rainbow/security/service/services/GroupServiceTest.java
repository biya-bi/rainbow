package org.rainbow.security.service.services;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.exceptions.DuplicateGroupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 
 * @author Biya-Bi
 *
 */
@DatabaseInitialize("src/test/resources/SQL/GroupServiceTestSetup.sql")
public class GroupServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("groupService")
	private GroupService groupService;

	@PersistenceContext
	private EntityManager em;

	private final Application application = new Application(4001L, "Test Application");

	@Test
	public void create_GroupIsValid_GroupCreated() throws Exception {
		Group group = new Group();
		group.setName("New Group 1");
		group.setApplication(application);

		groupService.create(group);

		Assert.assertNotNull(em.getReference(Group.class, group.getId()));
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ApplicationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Application missingApplication = new Application(Long.MAX_VALUE, "Missing Application");

		Group group = new Group();
		group.setName("New Group 1");
		group.setApplication(missingApplication);

		groupService.create(group);
	}

	@Test(expected = DuplicateGroupException.class)
	public void create_GroupAlreadyExists_ThrowDuplicateGroupException() throws Exception {
		Group group = new Group(null, "Sample Group 1");
		group.setApplication(application);

		try {
			groupService.create(group);
		} catch (DuplicateGroupException e) {
			Assert.assertEquals(group.getName(), e.getGroupName());
			throw e;
		}
	}

	@Test
	public void delete_GroupExists_GroupDeleted() throws Exception {
		// The below group represents Sample Group 2
		final Group group = em.getReference(Group.class, 4002L);
		boolean deleted = false;

		groupService.delete(group);

		try {
			em.getReference(Group.class, group.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_GroupDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Group group = new Group(Long.MAX_VALUE);

		groupService.delete(group);
	}

	@Test
	public void update_GroupIsValid_GroupUpdated() throws Exception {
		// Get sampleGroup3
		Group expected = em.getReference(Group.class, 4003L);
		expected.setName(expected.getName() + " Renamed");

		groupService.update(expected);

		Group actual = em.getReference(Group.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ApplicationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Application missingApplication = new Application(Long.MAX_VALUE, "Missing Application");

		// Get sampleGroup3
		Group group = em.getReference(Group.class, 4003L);
		group.setName(group.getName() + " Renamed");
		group.setApplication(missingApplication);

		groupService.update(group);
	}

	@Test(expected = DuplicateGroupException.class)
	public void update_GroupAlreadyExists_ThrowDuplicateGroupException() throws Exception {
		// Get sampleGroup3
		Group group = em.getReference(Group.class, 4003L);
		group.setName("Sample Group 1");

		try {
			groupService.update(group);
		} catch (DuplicateGroupException e) {
			Assert.assertEquals(group.getName(), e.getGroupName());
			throw e;
		}
	}

}