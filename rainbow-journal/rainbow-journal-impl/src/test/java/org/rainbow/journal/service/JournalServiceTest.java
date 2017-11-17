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
import org.rainbow.journal.service.exceptions.DuplicateJournalException;
import org.rainbow.journal.service.services.JournalService;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@DatabaseInitialize("src/test/resources/SQL/JournalServiceTestSetup.sql")
public class JournalServiceTest extends AbstractServiceTest {
	@Autowired
	@Qualifier("journalService")
	private JournalService journalService;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Test
	public void create_JournalIsValid_JournalCreated() throws Exception {
		Journal expected = new Journal();
		expected.setId(null);
		expected.setName("Academic Medicine");
		expected.setOwnerProfile(em.getReference(Profile.class, 2001L));

		journalService.create(expected);

		// If the create was successful, the expected.getId() method will return
		// a non-null value.
		Assert.assertNotNull(expected.getId());
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_OwnerProfileDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Journal expected = new Journal();
		expected.setId(null);
		expected.setName("Journal with non-existent owner profile");
		expected.setOwnerProfile(new Profile(Long.MAX_VALUE));

		journalService.create(expected);
	}

	@Test
	public void delete_JournalExists_JournalDeleted() throws Exception {
		final Journal journal = new Journal();
		journal.setId(2010L);
		boolean deleted = false;

		journalService.delete(journal);

		try {
			em.getReference(Journal.class, journal.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_JournalDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Journal journal = new Journal();
		journal.setId(Long.MAX_VALUE);
		journalService.delete(journal);
	}

	@Test
	public void update_JournalIsValid_JournalEdited() throws Exception {
		Journal expected = em.getReference(Journal.class, 2003L);
		expected.setName(expected.getName() + " Renamed");

		journalService.update(expected);

		Journal actual = em.getReference(Journal.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_OwnerProfileDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Journal expected = em.getReference(Journal.class, 2011L);
		expected.setOwnerProfile(new Profile(Long.MAX_VALUE));

		journalService.update(expected);
	}

	@Test
	public void find_NameExistsAndOperatorIsEqual_ReturnOneJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.equal(pathFactory.create("name"), "Acta Orthopaedica et Traumatologica Turcica"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEqual_ReturnNoJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.equal(pathFactory.create("name"), "non-existent-journal-name"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsNotEqual_ReturnJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("name"), "Acta Oto-Laryngologica"));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsNotEqual_ReturnAllJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("name"), "non-existent-journal-name"));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_NameExistsAndOperatorIsContains_ReturnTwoJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("name"), "Scandinavica"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsContains_ReturnNoJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("name"), "nonexistent"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsStartsWith_ReturnOneJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("name"), "acta"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertEquals(8, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsStartsWith_ReturnNoJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("name"), "non-existent-start"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsEndsWith_ReturnTwoJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("name"), "Scandinavica"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEndsWith_ReturnNoJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("name"), "non-existent-end"));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, "")));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder
				.not(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, ""))));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsDoesNotContain_ReturnJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.contains(pathFactory.create("name"), "Scandinavica")));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 3, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.contains(pathFactory.create("name"), "nonexistent")));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsToday_ReturnJournals() throws Exception {
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

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsNotToday_ReturnNoJournal() throws Exception {
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

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsLessThanToday_ReturnNoJournal() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThan(exp, lowerBound));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_CreationDateIsLessThanOrEqualToToday_ReturnJournals() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThanOrEqualTo(exp, upperBound));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsGreaterThanToday_ReturnNoJournal() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.greaterThan(exp, upperBound));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsGreaterThanOrEqualToToday_ReturnJournals() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.greaterThanOrEqualTo(exp, lowerBound));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenYesterdayAndTomorrow_ReturnJournals() throws Exception {
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

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenTenDaysBackAndYesterday_ReturnNoJournal() throws Exception {
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

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenYesterdayAndTomorrow_ReturnNoJournal() throws Exception {
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

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenTenDaysBackAndYesterday_ReturnJournals() throws Exception {
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

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(2005L, 2007L)));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoJournal() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE)));

		List<Journal> result = journalService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(2005L, 2007L))));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnJournals() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE))));

		List<Journal> result = journalService.find(searchOptions);
		List<Journal> result1 = journalService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test(expected = DuplicateJournalException.class)
	public void create_NameAlreadyExists_ThrowDuplicateJournalException() throws Exception {
		Journal expected = new Journal();
		expected.setId(null);
		expected.setName("ACIMED");
		expected.setOwnerProfile(em.getReference(Profile.class, 2001L));

		try {
			journalService.create(expected);
		} catch (DuplicateJournalException e) {
			Assert.assertEquals(expected.getName(), e.getName());
			throw e;
		}
	}

	@Test(expected = DuplicateJournalException.class)
	public void update_NameAlreadyExists_ThrowDuplicateJournalException() throws Exception {
		Journal expected = em.getReference(Journal.class, 2003L);
		expected.setName("Acta Anaesthesiologica Scandinavica");

		try {
			journalService.update(expected);
		} catch (DuplicateJournalException e) {
			Assert.assertEquals(expected.getName(), e.getName());
			throw e;
		}
	}
}
