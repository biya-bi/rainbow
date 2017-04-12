package org.rainbow.security.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.core.service.Service;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.persistence.exceptions.DuplicateGroupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class GroupServiceTest {

	@Autowired
	@Qualifier("groupService")
	private Service<Group, Long, SearchOptions> groupService;

	@PersistenceContext
	private EntityManager em;

	private final Application application = new Application(4001L, "Test Application");
	
	private static MySqlDatabase DATABASE;

	@Autowired
	public void initializeDatabase(MySqlDatabase mySqlDatabase)
			throws FileNotFoundException, SQLException, IOException {
		DATABASE = mySqlDatabase;
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/GroupServiceTestSetup.sql");
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
	public void create_GroupIsValid_GroupCreated() throws Exception {
		Group group = new Group();
		group.setName("New Group 1");
		group.setApplication(application);

		groupService.create(group);

		Assert.assertNotNull(em.getReference(Group.class, group.getId()));
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ApplicationDoesNotExist_ThrowNonexistentEntityException()
			throws DuplicateGroupException, Exception {
		final Application missingApplication = new Application(Long.MAX_VALUE, "Missing Application");

		Group group = new Group();
		group.setName("New Group 1");
		group.setApplication(missingApplication);

		groupService.create(group);
	}

	@Test(expected = DuplicateGroupException.class)
	public void create_GroupAlreadyExists_ThrowDuplicateGroupException() throws DuplicateGroupException, Exception {
		Group group = new Group(null, "Sample Group 1");
		group.setApplication(application);

		try {
			groupService.create(group);
		} catch (DuplicateGroupException e) {
			Assert.assertEquals(group.getName(), e.getGroupName());
			Assert.assertEquals(application.getName(), e.getApplicationName());
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
	public void update_ApplicationDoesNotExist_ThrowNonexistentEntityException()
			throws DuplicateGroupException, Exception {
		final Application missingApplication = new Application(Long.MAX_VALUE, "Missing Application");

		// Get sampleGroup3
		Group group = em.getReference(Group.class, 4003L);
		group.setName(group.getName() + " Renamed");
		group.setApplication(missingApplication);

		groupService.update(group);
	}

	@Test(expected = DuplicateGroupException.class)
	public void update_GroupAlreadyExists_ThrowDuplicateGroupException() throws DuplicateGroupException, Exception {
		// Get sampleGroup3
		Group group = em.getReference(Group.class, 4003L);
		group.setName("Sample Group 1");

		try {
			groupService.update(group);
		} catch (DuplicateGroupException e) {
			Assert.assertEquals(group.getName(), e.getGroupName());
			Assert.assertEquals(application.getName(), e.getApplicationName());
			throw e;
		}
	}

}