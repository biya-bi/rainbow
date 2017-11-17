package org.rainbow.journal.service;

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
import org.rainbow.criteria.Path;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.orm.entities.Subscription;
import org.rainbow.journal.service.services.SubscriptionService;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@DatabaseInitialize("src/test/resources/SQL/SubscriptionServiceTestSetup.sql")
public class SubscriptionServiceTest extends AbstractServiceTest {
	@Autowired
	@Qualifier("subscriptionService")
	private SubscriptionService subscriptionService;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Test
	public void create_SubscriptionIsValid_SubscriptionCreated() throws Exception {
		Subscription expected = new Subscription();
		expected.setId(null);
		expected.setJournal(em.getReference(Journal.class, 4005L));
		expected.setSubscriberProfile(em.getReference(Profile.class, 4001L));

		subscriptionService.create(expected);

		// If the create was successful, the expected.getId() method will return
		// a non-null value.
		Assert.assertNotNull(expected.getId());
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_PublisherSubscriptionDoesNotExist_ThrowNonexistentEntityException() throws Exception {
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
	public void update_PublisherSubscriptionDoesNotExist_ThrowNonexistentEntityException() throws Exception {
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
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder
				.equal(pathFactory.create("journal.name"), "Acta Orthopaedica et Traumatologica Turcica"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEqual_ReturnNoSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.equal(pathFactory.create("journal.name"), "non-existent-subscription-name"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsNotEqual_ReturnSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("journal.name"), "Acta Oto-Laryngologica"));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsNotEqual_ReturnAllSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("journal.name"), "non-existent-subscription-name"));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsContains_ReturnTwoSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("journal.name"), "Scandinavica"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsContains_ReturnNoSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("journal.name"), "nonexistent"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsStartsWith_ReturnOneSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("journal.name"), "acta"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertEquals(5, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsStartsWith_ReturnNoSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("journal.name"), "non-existent-start"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsEndsWith_ReturnTwoSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("journal.name"), "Scandinavica"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEndsWith_ReturnNoSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("journal.name"), "non-existent-end"));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("journal.name");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, "")));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("journal.name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder
				.not(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, ""))));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsDoesNotContain_ReturnSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.contains(pathFactory.create("journal.name"), "Scandinavica")));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.contains(pathFactory.create("journal.name"), "nonexistent")));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsToday_ReturnSubscriptions() throws Exception {
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

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsNotToday_ReturnNoSubscription() throws Exception {
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

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsLessThanToday_ReturnNoSubscription() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThan(exp, lowerBound));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_CreationDateIsLessThanOrEqualToToday_ReturnSubscriptions() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThanOrEqualTo(exp, upperBound));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsGreaterThanToday_ReturnNoSubscription() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.greaterThan(exp, upperBound));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsGreaterThanOrEqualToToday_ReturnSubscriptions() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.greaterThanOrEqualTo(exp, lowerBound));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenYesterdayAndTomorrow_ReturnSubscriptions() throws Exception {
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

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenTenDaysBackAndYesterday_ReturnNoSubscription() throws Exception {
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

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenYesterdayAndTomorrow_ReturnNoSubscription() throws Exception {
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

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenTenDaysBackAndYesterday_ReturnSubscriptions() throws Exception {
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

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(4005L, 4007L)));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoSubscription() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE)));

		List<Subscription> result = subscriptionService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(4005L, 4007L))));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnSubscriptions() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE))));

		List<Subscription> result = subscriptionService.find(searchOptions);
		List<Subscription> result1 = subscriptionService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

}
