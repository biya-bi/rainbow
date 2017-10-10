package org.rainbow.security.service.services;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.criteria.Expression;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.criteria.SearchOptionsImpl;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.LockoutPolicy;
import org.rainbow.security.orm.entities.PasswordPolicy;
import org.rainbow.security.service.exceptions.DuplicateApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@DatabaseInitialize("src/test/resources/SQL/ApplicationServiceTestSetup.sql")
public class ApplicationServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Test
	public void create_ApplicationIsValid_ApplicationCreated() throws Exception {
		Application expected = new Application();
		expected.setName("APP-NEW-1");
		expected.setPasswordPolicy(new PasswordPolicy());
		expected.setLockoutPolicy(new LockoutPolicy());

		applicationService.create(expected);

		Application actual = em.getReference(Application.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateApplicationException.class)
	public void create_NameAlreadyExists_ThrowDuplicateApplicationException() throws Exception {
		Application application = new Application();
		application.setName("Application 100");
		application.setPasswordPolicy(new PasswordPolicy());
		application.setLockoutPolicy(new LockoutPolicy());

		try {
			applicationService.create(application);
		} catch (DuplicateApplicationException e) {
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

	@Test(expected = DuplicateApplicationException.class)
	public void update_NameAlreadyExists_ThrowDuplicateApplicationException() throws Exception {
		Application application = em.getReference(Application.class, 1005L);
		application.setName("Application 103");

		try {
			applicationService.update(application);
		} catch (DuplicateApplicationException e) {
			Assert.assertEquals(application.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void find_NameExistsAndOperatorIsEqual_ReturnOneApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.equal(pathFactory.create("name"), "Rainbow Optimum"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEqual_ReturnNoApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.equal(pathFactory.create("name"), "non-existent-application-name"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsNotEqual_ReturnApplications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("name"), "rainbow optimum"));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsNotEqual_ReturnComplimentApplications() throws Exception {
		SearchOptionsImpl searchOptions = new SearchOptionsImpl();
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		searchOptions
				.setPredicate(predicateBuilder.notEqual(pathFactory.create("name"), "non-existent-application-name"));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_NameExistsAndOperatorIsContains_ReturnTwoApplications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.contains(exp, "optimum"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsContains_ReturnNoApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.contains(exp, "nonexistent"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsStartsWith_ReturnOneApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.startsWith(exp, "rain"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsStartsWith_ReturnNoApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(exp, "non-existent-start"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsEndsWith_ReturnTwoApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.endsWith(exp, "timum"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEndsWith_ReturnNoApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.endsWith(exp, "non-existent-end"));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.isNull(exp));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnApplications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.isNotNull(exp));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsDoesNotContain_ReturnApplications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.contains(exp, "optimum")));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.contains(exp, "nonexistent")));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsToday_ReturnApplications() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.and(
				predicateBuilder.greaterThanOrEqualTo(exp, lowerBound), predicateBuilder.lessThan(exp, upperBound)));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsNotToday_ReturnNoApplication() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.not(predicateBuilder.and(
				predicateBuilder.greaterThanOrEqualTo(exp, lowerBound), predicateBuilder.lessThan(exp, upperBound))));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_DateIsLessThanToday_ReturnNoApplication() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThan(exp, lowerBound));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_CreationDateIsLessThanOrEqualToToday_ReturnApplications() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThanOrEqualTo(exp, upperBound));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsGreaterThanToday_ReturnNoApplication() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.greaterThan(exp, upperBound));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsGreaterThanOrEqualToToday_ReturnApplications() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.greaterThanOrEqualTo(exp, lowerBound));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenYesterdayAndTomorrow_ReturnApplications() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);

		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime(); // Setting the amount 2 brings back
												// the date to the initial date,
												// and then increases it by 1 to
												// make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.between(exp, yesterday, tomorrow));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenTenDaysBackAndYesterday_ReturnNoApplication() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);

		// Set the date 10 days back.
		calendar.add(Calendar.DATE, -10);
		Date tenDaysBack = calendar.getTime();

		calendar.add(Calendar.DATE, 9);
		Date yesterday = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.between(exp, tenDaysBack, yesterday));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenYesterdayAndTomorrow_ReturnNoApplication() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);

		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();

		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.between(exp, yesterday, tomorrow)));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenTenDaysBackAndYesterday_ReturnApplications() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);

		// Set the date 10 days back.
		calendar.add(Calendar.DATE, -10);
		Date tenDaysBack = calendar.getTime();

		calendar.add(Calendar.DATE, 9);
		Date yesterday = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.between(exp, tenDaysBack, yesterday)));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnApplications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(1005L, 1007L)));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoApplication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE)));

		List<Application> result = applicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnApplications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(1005L, 1007L))));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnApplications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE))));

		List<Application> result = applicationService.find(searchOptions);
		List<Application> result1 = applicationService.find(new SearchOptionsImpl());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

}
