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
import org.rainbow.asset.explorer.core.entities.Manufacturer;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateManufacturerNameException;
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
@DatabaseInitialize("src/test/resources/ManufacturerServiceTestSetup.sql")
public class ManufacturerServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("manufacturerService")
	private Service<Manufacturer, Long, SearchOptions> manufacturerService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_ManufacturerIsValid_ManufacturerCreated() throws Exception {
		Manufacturer expected = new Manufacturer("LOC-NEW-ACC1");

		manufacturerService.create(expected);

		Manufacturer actual = em.getReference(Manufacturer.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateManufacturerNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateManufacturerNameException() throws Exception {
		Manufacturer manufacturer = new Manufacturer("Manufacturer 9001");

		try {
			manufacturerService.create(manufacturer);
		} catch (DuplicateManufacturerNameException e) {
			Assert.assertEquals(manufacturer.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void delete_ManufacturerExists_ManufacturerDeleted() throws Exception {
		final Manufacturer manufacturer = new Manufacturer(9002L);
		boolean deleted = false;

		manufacturerService.delete(manufacturer);

		try {
			em.getReference(Manufacturer.class, manufacturer.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ManufacturerDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Manufacturer manufacturer = new Manufacturer(5000L);
		manufacturerService.delete(manufacturer);
	}

	@Test
	public void update_ManufacturerIsValid_ManufacturerEdited() throws Exception {
		Manufacturer expected = em.getReference(Manufacturer.class, 9003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		manufacturerService.update(expected);

		Manufacturer actual = em.getReference(Manufacturer.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateManufacturerNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateManufacturerNameException() throws Exception {
		Manufacturer manufacturer = em.getReference(Manufacturer.class, 9005L);
		manufacturer.setName("Manufacturer 9004");

		try {
			manufacturerService.update(manufacturer);
		} catch (DuplicateManufacturerNameException e) {
			Assert.assertEquals(manufacturer.getName(), e.getName());
			throw e;
		}
	}
}
