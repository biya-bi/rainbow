/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.rainbow.security.core.entities.Authority;
import org.rainbow.security.core.persistence.exceptions.DuplicateAuthorityNameException;
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
public class AuthorityServiceTest {

	@Autowired
	@Qualifier("authorityService")
	private Service<Authority, Long, SearchOptions> authorityService;

	@PersistenceContext
	private EntityManager em;

	private final Application application = new Application(2001L, "Test Application");

	private static MySqlDatabase DATABASE;

	@Autowired
	public void initializeDatabase(MySqlDatabase mySqlDatabase)
			throws FileNotFoundException, SQLException, IOException {
		DATABASE = mySqlDatabase;
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/AuthorityServiceTestSetup.sql");
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
	public void create_AuthorityIsValid_AuthorityCreated() throws Exception {
		Authority expected = new Authority("VDR-NEW-ACC1");
		expected.setApplication(application);

		authorityService.create(expected);

		Authority actual = em.getReference(Authority.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateAuthorityNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateAuthorityNameException() throws Exception {
		Authority authority = new Authority("Authority 100");
		authority.setApplication(application);
		try {
			authorityService.create(authority);
		} catch (DuplicateAuthorityNameException e) {
			Assert.assertEquals(authority.getName(), e.getAuthorityName());
			Assert.assertEquals(authority.getApplication().getName(), e.getApplicationName());
			throw e;
		}
	}

	@Test
	public void delete_AuthorityExists_AuthorityDeleted() throws Exception {
		final Authority authority = new Authority();
		authority.setId(2002L);

		boolean deleted = false;

		authorityService.delete(authority);

		try {
			em.getReference(Authority.class, authority.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_AuthorityDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Authority authority = new Authority();
		authority.setId(Long.MAX_VALUE);

		authorityService.delete(authority);
	}

	@Test
	public void update_AuthorityIsValid_AuthorityEdited() throws Exception {
		Authority expected = em.getReference(Authority.class, 2003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		authorityService.update(expected);

		Authority actual = em.getReference(Authority.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateAuthorityNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateAuthorityNameException() throws Exception {
		Authority authority = em.getReference(Authority.class, 2005L);
		authority.setName("Authority 103");

		try {
			authorityService.update(authority);
		} catch (DuplicateAuthorityNameException e) {
			Assert.assertEquals(authority.getName(), e.getAuthorityName());
			throw e;
		}
	}
}
