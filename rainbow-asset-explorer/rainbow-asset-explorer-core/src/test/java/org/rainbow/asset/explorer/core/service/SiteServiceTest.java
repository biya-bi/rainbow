/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.core.entities.Site;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateSiteNameException;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SiteServiceTestSetup.sql")
public class SiteServiceTest extends AbstractServiceTest {

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("siteService")
	private Service<Site, Long, SearchOptions> siteService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_SiteIsValid_SiteCreated() throws Exception {
		Site expected = new Site("SITE-NEW-1");

		siteService.create(expected);

		Site actual = em.getReference(Site.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateSiteNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateSiteNameException() throws Exception {
		Site site = new Site("Site 16001");

		try {
			siteService.create(site);
		} catch (DuplicateSiteNameException e) {
			Assert.assertEquals(site.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void delete_SiteExists_SiteDeleted() throws Exception {
		final Site site = new Site(16002L);
		boolean deleted = false;

		siteService.delete(site);

		try {
			em.getReference(Site.class, site.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_SiteDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Site site = new Site(NON_EXISTENT_LONG_ID);
		siteService.delete(site);
	}

	@Test
	public void update_SiteIsValid_SiteEdited() throws Exception {
		Site expected = em.getReference(Site.class, 16003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		siteService.update(expected);

		Site actual = em.getReference(Site.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateSiteNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateSiteNameException() throws Exception {
		Site site = em.getReference(Site.class, 16005L);
		site.setName("Site 16004");

		try {
			siteService.update(site);
		} catch (DuplicateSiteNameException e) {
			Assert.assertEquals(site.getName(), e.getName());
			throw e;
		}
	}
}
