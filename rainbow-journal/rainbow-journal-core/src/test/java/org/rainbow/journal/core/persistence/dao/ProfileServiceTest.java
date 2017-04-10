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
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.persistence.exceptions.DuplicateProfileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class ProfileServiceTest {
	@Autowired
	@Qualifier("profileService")
	private Service<Profile, Long, SearchOptions> profileService;

	@PersistenceContext
	private EntityManager em;

	private static MySqlDatabase DATABASE = new MySqlDatabase();

	@BeforeClass
	public static void setUpClass() throws SQLException, IOException {
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/ProfileServiceTestSetup.sql");
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
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("jack.wagner");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsEqual_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue("non-existent-user-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsNotEqual_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("jackson.smith");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 1, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsNotEqual_ReturnAllProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue("non-existent-user-name");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsContains_ReturnTwoProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("jack");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsContains_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.CONTAINS);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsStartsWith_ReturnOneProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("isaac");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsStartsWith_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.STARTS_WITH);
		filter.setValue("non-existent-start");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsEndsWith_ReturnTwoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("mith");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsEndsWith_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.ENDS_WITH);
		filter.setValue("non-existent-end");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsEmpty_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.IS_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_OperatorIsNotEmpty_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.IS_NOT_EMPTY);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		Assert.assertFalse(result.isEmpty());
	}

	@Test
	public void find_UserNameExistsAndOperatorIsDoesNotContain_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("mith");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_UserNameDoesnotExistAndOperatorIsDoesNotContain_ReturnAllProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("userName");
		filter.setOperator(RelationalOperator.DOES_NOT_CONTAIN);
		filter.setValue("nonexistent");
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertFalse(result.isEmpty());
		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsEqual_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsNotEqual_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.NOT_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThan_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsLessThanOrEqual_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.LESS_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThan_ReturnNoProfile() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void find_DateIsGivenAndOperatorIsGreaterThanOrEqual_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Date> filter = new SingleValuedFilter<>();
		filter.setFieldName("creationDate");
		filter.setOperator(RelationalOperator.GREATER_THAN_OR_EQUAL);
		filter.setValue(new Date());
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsBetween_ReturnProfiles() throws Exception {
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
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsBetween_ReturnNoProfile() throws Exception {
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
		List<Profile> result = profileService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ValidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnNoProfile() throws Exception {
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
		List<Profile> result = profileService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_InvalidDateRangeIsGivenAndOperatorIsIsNotBetween_ReturnProfiles() throws Exception {
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
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsIn_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.IN);

		List<Long> ids = new ArrayList<>();
		ids.add(1005L);
		ids.add(1007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);

		Assert.assertEquals(2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsIn_ReturnNoProfile() throws Exception {
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
		List<Profile> result = profileService.find(options);

		Assert.assertTrue(result.isEmpty());
	}

	@Test
	public void find_ListOfExistingIdsIsGivenAndOperatorIsNotIn_ReturnProfiles() throws Exception {
		SearchOptions options = new SearchOptions();
		ListValuedFilter<Long> filter = new ListValuedFilter<>();
		filter.setFieldName("id");
		filter.setOperator(RelationalOperator.NOT_IN);

		List<Long> ids = new ArrayList<>();
		ids.add(1005L);
		ids.add(1007L);
		filter.setList(ids);

		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		options.setFilters(filters);
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size() - 2, result.size());
	}

	@Test
	public void find_ListOfNonExistingIdsIsGivenAndOperatorIsNotIn_ReturnProfiles() throws Exception {
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
		List<Profile> result = profileService.find(options);
		List<Profile> result1 = profileService.find(new SearchOptions());

		Assert.assertEquals(result1.size(), result.size());
	}

}
