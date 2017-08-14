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
import org.rainbow.asset.explorer.core.entities.ShipMethod;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateShipMethodNameException;
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
@DatabaseInitialize("src/test/resources/ShipMethodServiceTestSetup.sql")
public class ShipMethodServiceTest extends AbstractServiceTest {

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("shipMethodService")
	private Service<ShipMethod, Long, SearchOptions> shipMethodService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_ShipMethodIsValid_ShipMethodCreated() throws Exception {
		ShipMethod expected = new ShipMethod("VDR-NEW-ACC1");

		shipMethodService.create(expected);

		ShipMethod actual = em.getReference(ShipMethod.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateShipMethodNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateShipMethodNameException() throws Exception {
		ShipMethod shipMethod = new ShipMethod("Ship Method 14001");

		try {
			shipMethodService.create(shipMethod);
		} catch (DuplicateShipMethodNameException e) {
			Assert.assertEquals(shipMethod.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void delete_ShipMethodExists_ShipMethodDeleted() throws Exception {
		final ShipMethod shipMethod = new ShipMethod(14002L);
		boolean deleted = false;

		shipMethodService.delete(shipMethod);

		try {
			em.getReference(ShipMethod.class, shipMethod.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ShipMethodDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final ShipMethod shipMethod = new ShipMethod(NON_EXISTENT_LONG_ID);
		shipMethodService.delete(shipMethod);
	}

	@Test
	public void update_ShipMethodIsValid_ShipMethodEdited() throws Exception {
		ShipMethod expected = em.getReference(ShipMethod.class, 14003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		shipMethodService.update(expected);

		ShipMethod actual = em.getReference(ShipMethod.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateShipMethodNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateShipMethodNameException() throws Exception {
		ShipMethod shipMethod = em.getReference(ShipMethod.class, 14005L);
		shipMethod.setName("Ship Method 14004");

		try {
			shipMethodService.update(shipMethod);
		} catch (DuplicateShipMethodNameException e) {
			Assert.assertEquals(shipMethod.getName(), e.getName());
			throw e;
		}
	}
}
