package org.rainbow.asset.explorer.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.service.exceptions.DuplicateVendorAccountNumberException;
import org.rainbow.asset.explorer.service.services.VendorService;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/VendorServiceTestSetup.sql")
public class VendorServiceTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("vendorService")
	private VendorService vendorService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;
	
	@Test
	public void create_VendorIsValid_VendorCreated() throws Exception {
		Vendor expected = new Vendor("VDR-NEW-ACC1", "New Vendor", true, "www.optimum.org");

		vendorService.create(expected);

		Vendor actual = em.getReference(Vendor.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateVendorAccountNumberException.class)
	public void create_AccountNumberAlreadyExists_ThrowDuplicateVendorAccountNumberException() throws Exception {
		Vendor vendor = new Vendor("VDR-17001", "New Duplicate Account Vendor", true, "www.optimum.org");

		try {
			vendorService.create(vendor);
		} catch (DuplicateVendorAccountNumberException e) {
			Assert.assertEquals(vendor.getAccountNumber(), e.getAccountNumber());
			throw e;
		}
	}

	@Test
	public void delete_VendorExists_VendorDeleted() throws Exception {
		final Vendor vendor = new Vendor(17002L);
		boolean deleted = false;

		vendorService.delete(vendor);

		try {
			em.getReference(Vendor.class, vendor.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_VendorDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Vendor vendor = new Vendor(5000L);
		vendorService.delete(vendor);
	}

	@Test
	public void update_VendorIsValid_VendorEdited() throws Exception {
		Vendor expected = em.getReference(Vendor.class, 17003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		vendorService.update(expected);

		Vendor actual = em.getReference(Vendor.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateVendorAccountNumberException.class)
	public void update_VendorAccountNumberAlreadyExists_ThrowDuplicateVendorAccountNumberException() throws Exception {
		Vendor vendor = em.getReference(Vendor.class, 17005L);
		vendor.setAccountNumber("VDR-17004");

		try {
			vendorService.update(vendor);
		} catch (DuplicateVendorAccountNumberException e) {
			Assert.assertEquals(vendor.getAccountNumber(), e.getAccountNumber());
			throw e;
		}
	}

	@Test
	public void find_NameExistsAndOperatorIsEqual_ReturnOneVendor() throws Exception {
		PredicateBuilder builder = predicateBuilderFactory.create();

		SearchOptions searchOptions = searchOptionsFactory
				.create(builder.equal(pathFactory.create("name"), "Vendor 17001"));
		
		List<Vendor> result = vendorService.find(searchOptions);
		
		Assert.assertEquals(1, result.size());
	}

	@Test
	public void find_EmptyStringProvidedAndOperatorIsContains_ReturnVendors() throws Exception {
		PredicateBuilder builder = predicateBuilderFactory.create();

		SearchOptions searchOptions = searchOptionsFactory
				.create(builder.contains(pathFactory.create("name"), ""));
		
		List<Vendor> result = vendorService.find(searchOptions);
		
		Assert.assertFalse(result.isEmpty());
	}
}
