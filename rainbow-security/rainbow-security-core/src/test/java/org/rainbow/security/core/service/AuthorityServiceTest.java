/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.core.service.Service;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Authority;
import org.rainbow.security.core.persistence.exceptions.DuplicateAuthorityNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/AuthorityServiceTestSetup.sql")
public class AuthorityServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("authorityService")
	private Service<Authority, Long, SearchOptions> authorityService;

	@PersistenceContext
	private EntityManager em;

	private final Application application = new Application(2001L, "Test Application");

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
