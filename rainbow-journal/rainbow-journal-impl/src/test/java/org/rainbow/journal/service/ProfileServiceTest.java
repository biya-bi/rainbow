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
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.service.exceptions.DuplicateProfileException;
import org.rainbow.journal.service.services.ProfileService;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@DatabaseInitialize("src/test/resources/SQL/ProfileServiceTestSetup.sql")
public class ProfileServiceTest extends AbstractServiceTest {
	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Test
	public void create_ProfileIsValid_ProfileCreated() throws Exception {
		Profile expected = new Profile();
		expected.setId(null);
		expected.setUserName("new_sample_user");
		expected.setLastName("LastName");

		profileService.create(expected);

		// If the create was successful, the expected.getId() method will return
		// a non-null value.
		Assert.assertNotNull(expected.getId());
	}

	@Test(expected = DuplicateProfileException.class)
	public void create_UserNameAlreadyExists_ThrowDuplicateProfileException() throws Exception {
		Profile profile = new Profile();
		profile.setUserName("sample_user1");
		profile.setLastName("User1");

		try {
			profileService.create(profile);
		} catch (DuplicateProfileException e) {
			Assert.assertEquals(profile.getUserName(), e.getUserName());
			throw e;
		}
	}

	@Test
	public void delete_ProfileExists_ProfileDeleted() throws Exception {
		final Profile profile = new Profile();
		profile.setId(1002L);
		boolean deleted = false;

		profileService.delete(profile);

		try {
			em.getReference(Profile.class, profile.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ProfileDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Profile profile = new Profile();
		profile.setId(Long.MAX_VALUE);
		profileService.delete(profile);
	}

	@Test
	public void update_ProfileIsValid_ProfileEdited() throws Exception {
		Profile expected = em.getReference(Profile.class, 1003L);
		expected.setLastName(expected.getLastName() + " Renamed");

		profileService.update(expected);

		Profile actual = em.getReference(Profile.class, expected.getId());
		Assert.assertEquals(expected.getLastName(), actual.getLastName());
	}

	@Test(expected = DuplicateProfileException.class)
	public void update_UserNameAlreadyExists_ThrowDuplicateProfileException() throws Exception {
		Profile profile = em.getReference(Profile.class, 1004L);
		profile.setUserName("sample_user1");

		try {
			profileService.update(profile);
		} catch (DuplicateProfileException e) {
			Assert.assertEquals(profile.getUserName(), e.getUserName());
			throw e;
		}
	}

	@Test
	public void find_UserNameExistsAndOperatorIsEqual_ReturnOneProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.equal(pathFactory.create("userName"), "jack.wagner"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsEqual_ReturnNoProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.equal(pathFactory.create("userName"), "non-existent-user-name"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsNotEqual_ReturnProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("userName"), "jackson.smith"));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsNotEqual_ReturnAllProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.notEqual(pathFactory.create("userName"), "non-existent-user-name"));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsContains_ReturnTwoProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("userName"), "jack"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsContains_ReturnNoProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.contains(pathFactory.create("userName"), "nonexistent"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsStartsWith_ReturnOneProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("userName"), "isaac"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsStartsWith_ReturnNoProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.startsWith(pathFactory.create("userName"), "non-existent-start"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsEndsWith_ReturnTwoProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("userName"), "mith"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsEndsWith_ReturnNoProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.endsWith(pathFactory.create("userName"), "non-existent-end"));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("userName");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, "")));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final Path path = pathFactory.create("userName");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder
				.not(predicateBuilder.or(predicateBuilder.isNull(path), predicateBuilder.equal(path, ""))));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsDoesNotContain_ReturnProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.contains(pathFactory.create("userName"), "mith")));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.contains(pathFactory.create("userName"), "nonexistent")));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsToday_ReturnProfiles() throws Exception {
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

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsNotToday_ReturnNoProfile() throws Exception {
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

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsLessThanToday_ReturnNoProfile() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThan(exp, lowerBound));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_CreationDateIsLessThanOrEqualToToday_ReturnProfiles() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.lessThanOrEqualTo(exp, upperBound));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsGreaterThanToday_ReturnNoProfile() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23,
				59, 59);
		Date upperBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory.create(predicateBuilder.greaterThan(exp, upperBound));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsGreaterThanOrEqualToToday_ReturnProfiles() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0,
				0, 0);
		Date lowerBound = calendar.getTime();

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<String> exp = pathFactory.create("creationDate");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.greaterThanOrEqualTo(exp, lowerBound));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenYesterdayAndTomorrow_ReturnProfiles() throws Exception {
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

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_CreationDateIsBetweenTenDaysBackAndYesterday_ReturnNoProfile() throws Exception {
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

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenYesterdayAndTomorrow_ReturnNoProfile() throws Exception {
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

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_CreationDateIsNotBetweenTenDaysBackAndYesterday_ReturnProfiles() throws Exception {
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

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(1005L, 1007L)));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoProfile() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE)));

		List<Profile> result = profileService.find(searchOptions);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(1005L, 1007L))));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnProfiles() throws Exception {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		Expression<?> exp = pathFactory.create("id");
		SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.not(predicateBuilder.in(exp, Arrays.asList(Long.MAX_VALUE - 1, Long.MAX_VALUE))));

		List<Profile> result = profileService.find(searchOptions);
		List<Profile> result1 = profileService.find(searchOptionsFactory.create());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

}
