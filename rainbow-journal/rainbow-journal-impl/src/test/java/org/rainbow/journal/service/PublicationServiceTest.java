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
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.service.services.PublicationService;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@DatabaseInitialize("src/test/resources/SQL/PublicationServiceTestSetup.sql")
public class PublicationServiceTest extends AbstractServiceTest {
	@Autowired
	@Qualifier("publicationService")
	private PublicationService publicationService;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

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
	public void create_PublisherPublicationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
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
	public void update_PublisherPublicationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
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
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder
				.equal(pathFactory.create("journal.name"), "Acta Orthopaedica et Traumatologica Turcica"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEqual_ReturnNoPublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.equal(pathFactory.create("journal.name"), "non-existent-publication-name"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsNotEqual_ReturnPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("journal.name"), "Acta Oto-Laryngologica"));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsNotEqual_ReturnAllPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("journal.name"), "non-existent-publication-name"));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsContains_ReturnTwoPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("journal.name"), "Scandinavica"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsContains_ReturnNoPublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("journal.name"), "nonexistent"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsStartsWith_ReturnOnePublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("journal.name"), "acta"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertEquals(5, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsStartsWith_ReturnNoPublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("journal.name"), "non-existent-start"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsEndsWith_ReturnTwoPublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("journal.name"), "Scandinavica"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertEquals(3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsEndsWith_ReturnNoPublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("journal.name"), "non-existent-end"));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoPublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("journal.name");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, "")));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("journal.name");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder
				.not(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, ""))));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_JournalNameExistsAndOperatorIsDoesNotContain_ReturnPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.contains(pathFactory.create("journal.name"), "Scandinavica")));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 3, result.size());
	}

	@Test
	public void find_JournalNameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.contains(pathFactory.create("journal.name"), "nonexistent")));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsToday_ReturnPublications() throws Exception {
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

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsNotToday_ReturnNoPublication() throws Exception {
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

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsLessThanToday_ReturnNoPublication() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThan(exp, lowerBound));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_CreationDateIsLessThanOrEqualToToday_ReturnPublications() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThanOrEqualTo(exp, upperBound));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsGreaterThanToday_ReturnNoPublication() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.greaterThan(exp, upperBound));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsGreaterThanOrEqualToToday_ReturnPublications() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.greaterThanOrEqualTo(exp, lowerBound));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenYesterdayAndTomorrow_ReturnPublications() throws Exception {
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

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenTenDaysBackAndYesterday_ReturnNoPublication() throws Exception {
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

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenYesterdayAndTomorrow_ReturnNoPublication() throws Exception {
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

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenTenDaysBackAndYesterday_ReturnPublications() throws Exception {
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

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(3005L, 3007L)));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoPublication() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE)));

		List<Publication> result = publicationService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(3005L, 3007L))));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnPublications() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE))));

		List<Publication> result = publicationService.find(searchOptions);
		List<Publication> result1 = publicationService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

}
