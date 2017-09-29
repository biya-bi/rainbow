package org.rainbow.asset.explorer.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocationNameException;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/LocationServiceTestSetup.sql")
public class LocationServiceTest extends AbstractServiceTest {

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("locationService")
	private Service<Location, Long, SearchOptions> locationService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_LocationIsValid_LocationCreated() throws Exception {
		Location expected = new Location("LOC-NEW-ACC1");

		locationService.create(expected);

		Location actual = em.getReference(Location.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateLocationNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateLocationNameException() throws Exception {
		Location location = new Location("Location 8001");

		try {
			locationService.create(location);
		} catch (DuplicateLocationNameException e) {
			Assert.assertEquals(location.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void delete_LocationExists_LocationDeleted() throws Exception {
		final Location location = new Location(8002L);
		boolean deleted = false;

		locationService.delete(location);

		try {
			em.getReference(Location.class, location.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_LocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Location location = new Location(NON_EXISTENT_LONG_ID);
		locationService.delete(location);
	}

	@Test
	public void update_LocationIsValid_LocationEdited() throws Exception {
		Location expected = em.getReference(Location.class, 8003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		locationService.update(expected);

		Location actual = em.getReference(Location.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateLocationNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateLocationNameException() throws Exception {
		Location location = em.getReference(Location.class, 8005L);
		location.setName("Location 8004");

		try {
			locationService.update(location);
		} catch (DuplicateLocationNameException e) {
			Assert.assertEquals(location.getName(), e.getName());
			throw e;
		}
	}
}
