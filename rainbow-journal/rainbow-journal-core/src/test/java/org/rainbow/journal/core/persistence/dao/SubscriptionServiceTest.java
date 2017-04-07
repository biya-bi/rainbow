package org.rainbow.journal.core.persistence.dao;

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
import org.junit.BeforeClass;
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
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.entities.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class SubscriptionServiceTest {
	@Autowired
	@Qualifier("subscriptionService")
	private Service<Subscription, Long, SearchOptions> subscriptionService;

	@PersistenceContext
	private EntityManager em;

	private static MySqlDatabase DATABASE = new MySqlDatabase();

	@BeforeClass
	public static void setUpClass() throws SQLException, IOException {
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/SubscriptionServiceTestSetup.sql");
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
	public void create_SubscriptionIsValid_SubscriptionCreated() throws Exception {
		Subscription expected = new Subscription();
		expected.setId(null);
		expected.setJournal(em.getReference(Journal.class, 4001L));
		expected.setSubscriberProfile(em.getReference(Profile.class, 4001L));

		subscriptionService.create(expected);

		// If the create was successful, the expected.getId() method will return
		// a non-null value.
		Assert.assertNotNull(expected.getId());
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_SubscriberProfileDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Subscription expected = new Subscription();
		expected.setId(null);
		expected.setJournal(em.getReference(Journal.class, 4001L));
		expected.setSubscriberProfile(new Profile(Long.MAX_VALUE));

		subscriptionService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_JournalDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Subscription expected = new Subscription();
		expected.setId(null);
		expected.setJournal(new Journal(Long.MAX_VALUE));
		expected.setSubscriberProfile(em.getReference(Profile.class, 4001L));

		subscriptionService.create(expected);
	}

	@Test
	public void delete_SubscriptionExists_SubscriptionDeleted() throws Exception {
		final Subscription subscription = new Subscription();
		subscription.setId(4010L);
		boolean deleted = false;

		subscriptionService.delete(subscription);

		try {
			em.getReference(Subscription.class, subscription.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_SubscriptionDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Subscription subscription = new Subscription();
		subscription.setId(Long.MAX_VALUE);
		subscriptionService.delete(subscription);
	}

	@Test
	public void update_SubscriptionIsValid_SubscriptionEdited() throws Exception {
		Subscription expected = em.getReference(Subscription.class, 4003L);
		expected.setSubscriptionDate(new Date());

		subscriptionService.update(expected);

		Subscription actual = em.getReference(Subscription.class, expected.getId());
		Assert.assertEquals(expected.getSubscriptionDate(), actual.getSubscriptionDate());
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_SubscriberProfileDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Subscription expected = em.getReference(Subscription.class, 4011L);
		expected.setSubscriberProfile(new Profile(Long.MAX_VALUE));

		subscriptionService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_JournalDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Subscription expected = em.getReference(Subscription.class, 4011L);
		expected.setJournal(new Journal(Long.MAX_VALUE));

		subscriptionService.update(expected);
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsEqual_ReturnOneSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("Acta Orthopaedica et Traumatologica Turcica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEqual_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("non-existent-subscription-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsNotEqual_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("Acta Oto-Laryngologica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsNotEqual_ReturnAllSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("non-existent-subscription-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsContains_ReturnTwoSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsContains_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsStartsWith_ReturnOneSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("acta");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertEquals(5, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsStartsWith_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("non-existent-start");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsEndsWith_ReturnTwoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEndsWith_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("non-existent-end");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.IS_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.IS_NOT_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsDoesNotContain_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsEqual_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsNotEqual_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThan_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThanOrEqual_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThan_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThanOrEqual_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsBetween_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		RangeValuedFilter<Date> filter = new RangeValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.IS_BETWEEN);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();

		// Setting the amount 2 brings back the date to the initial date, and
		// then increases it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsBetween_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
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
		// then increases it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		RangeValuedFilter<Date> filter = new RangeValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.IS_NOT_BETWEEN);
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		Date yesterday = calendar.getTime();
		// Setting the amount 2 brings back the date to the initial date, and
		// then increases it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
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
		// then increases it by 1 to make it the next day.
		calendar.add(Calendar.DATE, 2);
		Date tomorrow = calendar.getTime();

		filter.setLowerBound(yesterday);
		filter.setUpperBound(tomorrow);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.IN);

		List<Long> ids = new ArrayList<>();
		ids.add(4005L);
		ids.add(4007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoSubscription() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.IN);

		List<Long> ids = new ArrayList<>();
		ids.add(Long.MAX_VALUE - 1);
		ids.add(Long.MAX_VALUE);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.NOT_IN);

		List<Long> ids = new ArrayList<>();
		ids.add(4005L);
		ids.add(4007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnSubscriptions() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.NOT_IN);

		List<Long> ids = new ArrayList<>();
		ids.add(Long.MAX_VALUE - 1);
		ids.add(Long.MAX_VALUE);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Subscription> result = subscriptionService.find(options);
		List<Subscription> result1 = subscriptionService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

}
