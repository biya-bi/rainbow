package org.rainbow.journal.core.persistence.dao;

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
import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.ListValuedSearchCriterion;
import org.rainbow.core.persistence.RangeValuedSearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.persistence.exceptions.DuplicateJournalException;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class JournalServiceTest {
	@Autowired
	@Qualifier("journalService")
	private Service<Journal, Long, SearchOptions> journalService;

	@PersistenceContext
	private EntityManager em;

	private static MySqlDatabase DATABASE;

	@Autowired
	public void initializeDatabase(MySqlDatabase mySqlDatabase) throws FileNotFoundException, SQLException, IOException {
		DATABASE = mySqlDatabase;
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/JournalServiceTestSetup.sql");
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
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("Acta Orthopaedica et Traumatologica Turcica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEqual_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("non-existent-journal-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsNotEqual_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("Acta Oto-Laryngologica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsNotEqual_ReturnAllJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("non-existent-journal-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_NameExistsAndOperatorIsContains_ReturnTwoJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsContains_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsStartsWith_ReturnOneJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("acta");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertEquals(8, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsStartsWith_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("non-existent-start");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsEndsWith_ReturnTwoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsEndsWith_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("non-existent-end");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.IS_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.IS_NOT_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_NameExistsAndOperatorIsDoesNotContain_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 3, result.size());
	}

	@Test
	public void find_NameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsEqual_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsNotEqual_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThan_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThanOrEqual_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThan_ReturnNoJournal() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThanOrEqual_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsBetween_ReturnJournals() throws Exception {
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
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsBetween_ReturnNoJournal() throws Exception {
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
		List<Journal> result = journalService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnNoJournal() throws Exception {
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
		List<Journal> result = journalService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnJournals() throws Exception {
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
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.IN);

		List<Long> ids = new ArrayList<>();
		ids.add(2005L);
		ids.add(2007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoJournal() throws Exception {
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
		List<Journal> result = journalService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnJournals() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.NOT_IN);

		List<Long> ids = new ArrayList<>();
		ids.add(2005L);
		ids.add(2007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnJournals() throws Exception {
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
		List<Journal> result = journalService.find(options);
		List<Journal> result1 = journalService.find(new SearchOptions());

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
