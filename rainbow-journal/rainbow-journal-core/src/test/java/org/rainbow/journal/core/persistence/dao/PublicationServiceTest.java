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
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.ListValuedFilter;
import org.rainbow.core.persistence.RangeValuedFilter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.entities.Publication;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class PublicationServiceTest {
	@Autowired
	@Qualifier("publicationService")
	private Service<Publication, Long, SearchOptions> publicationService;

	@PersistenceContext
	private EntityManager em;

	private static MySqlDatabase DATABASE;

	@Autowired
	public void initializeDatabase(MySqlDatabase mySqlDatabase)
			throws FileNotFoundException, SQLException, IOException {
		DATABASE = mySqlDatabase;
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/PublicationServiceTestSetup.sql");
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
	public void create_PublicationIsValid_PublicationCreated() throws Exception {
		Publication expected = new Publication();
		expected.setId(null);
		expected.setJournal(em.getReference(Journal.class, 3001L));
		expected.setPublisherProfile(em.getReference(Profile.class, 3001L));

		publicationService.create(expected);

		// If the create was successful, the expected.getId() method will return
		// a non-null value.
		Assert.assertNotNull(expected.getId());
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_PublisherProfileDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Publication expected = new Publication();
		expected.setId(null);
		expected.setJournal(em.getReference(Journal.class, 3001L));
		expected.setPublisherProfile(new Profile(Long.MAX_VALUE));

		publicationService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_JournalDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Publication expected = new Publication();
		expected.setId(null);
		expected.setJournal(new Journal(Long.MAX_VALUE));
		expected.setPublisherProfile(em.getReference(Profile.class, 3001L));

		publicationService.create(expected);
	}

	@Test
	public void delete_PublicationExists_PublicationDeleted() throws Exception {
		final Publication publication = new Publication();
		publication.setId(3010L);
		boolean deleted = false;

		publicationService.delete(publication);

		try {
			em.getReference(Publication.class, publication.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_PublicationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Publication publication = new Publication();
		publication.setId(Long.MAX_VALUE);
		publicationService.delete(publication);
	}

	@Test
	public void update_PublicationIsValid_PublicationEdited() throws Exception {
		Publication expected = em.getReference(Publication.class, 3003L);
		expected.setPublicationDate(new Date());

		publicationService.update(expected);

		Publication actual = em.getReference(Publication.class, expected.getId());
		Assert.assertEquals(expected.getPublicationDate(), actual.getPublicationDate());
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_PublisherProfileDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Publication expected = em.getReference(Publication.class, 3011L);
		expected.setPublisherProfile(new Profile(Long.MAX_VALUE));

		publicationService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_JournalDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Publication expected = em.getReference(Publication.class, 3011L);
		expected.setJournal(new Journal(Long.MAX_VALUE));

		publicationService.update(expected);
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsEqual_ReturnOnePublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("Acta Orthopaedica et Traumatologica Turcica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEqual_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("non-existent-publication-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsNotEqual_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("Acta Oto-Laryngologica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsNotEqual_ReturnAllPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("non-existent-publication-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsContains_ReturnTwoPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsContains_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsStartsWith_ReturnOnePublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("acta");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertEquals(5, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsStartsWith_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("non-existent-start");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsEndsWith_ReturnTwoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEndsWith_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("non-existent-end");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.IS_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.IS_NOT_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsDoesNotContain_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("Scandinavica");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("journal.name");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsEqual_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsNotEqual_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThan_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThanOrEqual_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThan_ReturnNoPublication() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThanOrEqual_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsBetween_ReturnPublications() throws Exception {
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
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsBetween_ReturnNoPublication() throws Exception {
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
		List<Publication> result = publicationService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnNoPublication() throws Exception {
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
		List<Publication> result = publicationService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnPublications() throws Exception {
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
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.IN);

		List<Long> ids = new ArrayList<>();
		ids.add(3005L);
		ids.add(3007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoPublication() throws Exception {
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
		List<Publication> result = publicationService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnPublications() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.NOT_IN);

		List<Long> ids = new ArrayList<>();
		ids.add(3005L);
		ids.add(3007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnPublications() throws Exception {
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
		List<Publication> result = publicationService.find(options);
		List<Publication> result1 = publicationService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

}
