package org.rainbow.security.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.ListValuedFilter;
import org.rainbow.core.persistence.RangeValuedFilter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.core.service.Service;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.PasswordPolicy;
import org.rainbow.security.core.entities.TokenPolicy;
import org.rainbow.security.core.persistence.exceptions.DuplicateApplicationNameException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class ApplicationServiceTest {
	@Autowired
	@Qualifier("applicationService")
	private Service<Application, Long, SearchOptions> applicationService;

	@PersistenceContext
	private EntityManager em;

	private static MySqlDatabase DATABASE;

	@Autowired
	public void initializeDatabase(MySqlDatabase mySqlDatabase)
			throws FileNotFoundException, SQLException, IOException {
		DATABASE = mySqlDatabase;
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/ApplicationServiceTestSetup.sql");
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
	public void create_ApplicationIsValid_ApplicationCreated() throws Exception {
		Application expected = new Application();
		expected.setName("APP-NEW-1");
		expected.setPasswordPolicy(new PasswordPolicy());
		expected.setTokenPolicy(new TokenPolicy());

		applicationService.create(expected);

		Application actual = em.getReference(Application.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateApplicationNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateApplicationNameException() throws Exception {
		Application application = new Application();
		application.setName("Application 100");
		application.setPasswordPolicy(new PasswordPolicy());
		application.setTokenPolicy(new TokenPolicy());

		try {
			applicationService.create(application);
		} catch (DuplicateApplicationNameException e) {
			Assert.assertEquals(application.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void delete_ApplicationExists_ApplicationDeleted() throws Exception {
		final Application application = new Application();
		application.setId(1002L);
		boolean deleted = false;

		applicationService.delete(application);

		try {
			em.getReference(Application.class, application.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ApplicationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Application application = new Application();
		application.setId(Long.MAX_VALUE);
		applicationService.delete(application);
	}

	@Test
	public void update_ApplicationIsValid_ApplicationEdited() throws Exception {
		Application expected = em.getReference(Application.class, 1003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		applicationService.update(expected);

		Application actual = em.getReference(Application.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateApplicationNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateApplicationNameException() throws Exception {
		Application application = em.getReference(Application.class, 1005L);
		application.setName("Application 103");

		try {
			applicationService.update(application);
		} catch (DuplicateApplicationNameException e) {
			Assert.assertEquals(application.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void find_NameExistsAndOperatorIsEqual_ReturnOneApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("Rainbow Optimum");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEqual_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("non-existent-application-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsNotEqual_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("rainbow optimum");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsNotEqual_ReturnTwoApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("non-existent-application-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_NameExistsAndOperatorIsContains_ReturnTwoApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("optimum");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsContains_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsStartsWith_ReturnOneApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("rain");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsStartsWith_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("non-existent-start");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsEndsWith_ReturnTwoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("timum");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEndsWith_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("non-existent-end");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.IS_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.IS_NOT_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsDoesNotContain_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("optimum");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsEqual_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsNotEqual_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThan_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThanOrEqual_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThan_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThanOrEqual_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsBetween_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		RangeValuedFilter<Date> filter = new RangeValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.IS_BETWEEN);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		// Setting the amount 2 brings back the date to the initial date, and
		// then increases
		// it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsBetween_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		RangeValuedFilter<Date> filter = new RangeValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.IS_BETWEEN);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		// Set the date 10 days back.
		calendar.add(Calendar.DATE, -10);

		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();

		// Setting the amount 2 brings back the date to the initial date, and
		// then increases
		// it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		RangeValuedFilter<Date> filter = new RangeValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.IS_NOT_BETWEEN);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		// Setting the amount 2 brings back the date to the initial date, and
		// then increases
		// it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		RangeValuedFilter<Date> filter = new RangeValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.IS_NOT_BETWEEN);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(date);

		// Set the date 10 days back.
		calendar.add(Calendar.DATE, -10);

		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();

		// Setting the amount 2 brings back the date to the initial date, and
		// then increases
		// it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.IN);

		List<Long> ids = new ArrayList<>();
		ids.add(1005L);
		ids.add(1007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.IN);

		List<Long> ids = new ArrayList<>();
		ids.add(Long.MAX_VALUE - 1);
		ids.add(Long.MAX_VALUE);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.NOT_IN);

		List<Long> ids = new ArrayList<>();
		ids.add(1005L);
		ids.add(1007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnApplications() throws Exception {
		SearchOptions criteria = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.NOT_IN);

		List<Long> ids = new ArrayList<>();
		ids.add(Long.MAX_VALUE - 1);
		ids.add(Long.MAX_VALUE);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		List<Application> result1 = applicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}
}
