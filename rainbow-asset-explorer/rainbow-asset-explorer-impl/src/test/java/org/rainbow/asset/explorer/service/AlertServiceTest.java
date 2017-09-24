package org.rainbow.asset.explorer.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Alert;
import org.rainbow.asset.explorer.orm.entities.AlertCategory;
import org.rainbow.asset.explorer.orm.entities.AlertType;
import org.rainbow.asset.explorer.orm.entities.Schedule;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAlertException;
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
@DatabaseInitialize("src/test/resources/SQL/AlertServiceTestSetup.sql")
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
	public void create_TypeCategoryPairAlreadyExists_ThrowDuplicateAlertException() throws Exception {
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
