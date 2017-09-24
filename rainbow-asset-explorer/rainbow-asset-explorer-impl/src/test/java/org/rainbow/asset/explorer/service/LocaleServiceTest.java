package org.rainbow.asset.explorer.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocaleLcidException;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocaleNameException;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/LocaleServiceTestSetup.sql")
public class LocaleServiceTest extends AbstractServiceTest {

	private static final int NON_EXISTENT_INT_ID = 5000000;

	@Autowired
	@Qualifier("localeService")
	private Service<Locale, Integer, SearchOptions> localeService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_LocaleIsValid_LocaleCreated() throws Exception {
		Locale expected = new Locale("Arabic - Algeria", "ar", "ar-dz");

		localeService.create(expected);

		Locale actual = em.getReference(Locale.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateLocaleNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateLocaleNameException() throws Exception {
		Locale locale = new Locale("English - United States");

		try {
			localeService.create(locale);
		} catch (DuplicateLocaleNameException e) {
			Assert.assertEquals(locale.getName(), e.getName());
			throw e;
		}
	}

	@Test(expected = DuplicateLocaleLcidException.class)
	public void create_LcidAlreadyExists_ThrowDuplicateLocaleLcidException() throws Exception {
		Locale expected = new Locale("Arabic (Algeria)", "ar", "ar-ly");

		try {
			localeService.create(expected);
		} catch (DuplicateLocaleLcidException e) {
			Assert.assertEquals(expected.getLcid(), e.getLcid());
			throw e;
		}
	}

	@Test
	public void delete_LocaleExists_LocaleDeleted() throws Exception {
		final Locale locale = new Locale(7002);
		boolean deleted = false;

		localeService.delete(locale);

		try {
			em.getReference(Locale.class, locale.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_LocaleDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Locale locale = new Locale(NON_EXISTENT_INT_ID);
		localeService.delete(locale);
	}

	@Test
	public void update_LocaleIsValid_LocaleEdited() throws Exception {
		Locale expected = em.getReference(Locale.class, 7003);
		expected.setName("French (France)");

		em.clear();

		localeService.update(expected);

		Locale actual = em.getReference(Locale.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateLocaleNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateLocaleNameException() throws Exception {
		Locale locale = em.getReference(Locale.class, 7005);
		locale.setName("Chinese - China");

		try {
			localeService.update(locale);
		} catch (DuplicateLocaleNameException e) {
			Assert.assertEquals(locale.getName(), e.getName());
			throw e;
		}
	}

	@Test(expected = DuplicateLocaleLcidException.class)
	public void update_LcidAlreadyExists_ThrowDuplicateLocaleLcidException() throws Exception {
		Locale locale = em.getReference(Locale.class, 7007);
		locale.setLcid("ar-ly");

		try {
			localeService.update(locale);
		} catch (DuplicateLocaleLcidException e) {
			Assert.assertEquals(locale.getLcid(), e.getLcid());
			throw e;
		}
	}
}
