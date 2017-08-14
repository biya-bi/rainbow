/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.core.entities.Alert;
import org.rainbow.asset.explorer.core.entities.AlertCategory;
import org.rainbow.asset.explorer.core.entities.AlertType;
import org.rainbow.asset.explorer.core.entities.Schedule;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateAlertException;
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
@DatabaseInitialize("src/test/resources/AlertServiceTestSetup.sql")
public class AlertServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("alertService")
	private Service<Alert, Integer, SearchOptions> alertService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_AlertIsValid_AlertCreated() throws Exception {
		Alert expected = new Alert(AlertCategory.STOCK_LEVEL, AlertType.RECOVERY, true);
		expected.setSchedule(new Schedule());

		alertService.create(expected);

		Alert actual = em.getReference(Alert.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateAlertException.class)
	public void create_TypeCategoryPairAlreadyExists_ThrowDuplicateAlertNameException() throws Exception {
		Alert alert = new Alert(AlertCategory.STOCK_LEVEL, AlertType.WARNING, true);
		alert.setSchedule(new Schedule());

		try {
			alertService.create(alert);
		} catch (DuplicateAlertException e) {
			Assert.assertEquals(alert.getAlertCategory(), e.getAlertCategory());
			Assert.assertEquals(alert.getAlertType(), e.getAlertType());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_AlertDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Alert alert = new Alert(5000);
		alertService.delete(alert);
	}

	@Test
	public void update_AlertIsValid_AlertEdited() throws Exception {
		Alert expected = em.getReference(Alert.class, 1001);

		em.clear();

		expected.setImmediate(false);

		alertService.update(expected);

		Alert actual = em.getReference(Alert.class, expected.getId());
		actual.getId();
	}

	// @Test(expected = DuplicateAlertException.class)
	// public void
	// update_TypeCategoryPairAlreadyExists_ThrowDuplicateAlertException()
	// throws Exception {
	// Alert newAlert = new Alert(AlertCategory.STOCK_LEVEL, AlertType.RECOVERY,
	// true);
	// newAlert.setSchedule(new Schedule());
	//
	// alertService.create(newAlert);
	//
	// Alert alert = em.getReference(Alert.class, newAlert.getId());
	// alert.setAlertCategory(AlertCategory.STOCK_LEVEL);
	// alert.setAlertType(AlertType.WARNING);
	// try {
	// alertService.update(alert);
	// } catch (DuplicateAlertException e) {
	// Assert.assertEquals(alert.getAlertCategory(), e.getAlertCategory());
	// Assert.assertEquals(alert.getAlertType(), e.getAlertType());
	// throw e;
	// }
	// }
}
