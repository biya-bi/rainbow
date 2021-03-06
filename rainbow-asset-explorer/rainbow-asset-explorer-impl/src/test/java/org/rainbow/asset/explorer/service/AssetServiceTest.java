package org.rainbow.asset.explorer.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Asset;
import org.rainbow.asset.explorer.orm.entities.AssetType;
import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.Site;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAssetSerialNumberException;
import org.rainbow.asset.explorer.service.services.AssetService;
import org.rainbow.asset.explorer.util.PersistenceSettings;
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
@DatabaseInitialize("src/test/resources/SQL/AssetServiceTestSetup.sql")
public class AssetServiceTest extends AbstractServiceTest {

	private static final int NON_EXISTENT_INT_ID = 5000;

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("assetService")
	private AssetService assetService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	/**
	 * Test of create method, of class AssetDao.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void create_AssetIsValid_AssetCreated() throws Exception {
		Date now = new Date();
		Asset expected = new Asset("New Asset 1", "ASS-1500", now, now);
		expected.setProduct(new Product(2001L));

		assetService.create(expected);

		Asset actual = em.getReference(Asset.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test
	public void delete_AssetExists_AssetDeleted() throws Exception {
		// The below asset represents Sample Asset 2
		final Asset asset = new Asset(2002L);
		boolean deleted = false;

		assetService.delete(asset);

		try {
			em.getReference(Asset.class, asset.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_AssetDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Asset asset = new Asset(NON_EXISTENT_LONG_ID);
		assetService.delete(asset);
	}

	@Test
	public void update_AssetIsValid_AssetEdited() throws Exception {
		// Get Sample Asset 3
		Asset expected = em.getReference(Asset.class, 2003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		assetService.update(expected);

		Asset actual = em.getReference(Asset.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateAssetSerialNumberException.class)
	public void create_AssetSerialNumberAlreadyExists_ThrowDuplicateAssetSerialNumberException() throws Exception {
		Date now = new Date();
		Asset asset = new Asset("Asset with duplicate serial number", "ASS-2006", now, now);
		asset.setProduct(new Product(2001L));

		try {
			assetService.create(asset);
		} catch (DuplicateAssetSerialNumberException e) {
			Assert.assertEquals(asset.getSerialNumber(), e.getSerialNumber());
			throw e;
		}
	}

	@Test(expected = DuplicateAssetSerialNumberException.class)
	public void update_AssetSerialNumberAlreadyExists_ThrowDuplicateAssetNumberException() throws Exception {
		// Get Sample Asset 8
		Asset asset = em.getReference(Asset.class, 2008L);
		asset.setSerialNumber("ASS-2007");

		try {
			assetService.update(asset);
		} catch (DuplicateAssetSerialNumberException e) {
			Assert.assertEquals(asset.getSerialNumber(), e.getSerialNumber());
			throw e;
		}
	}

	@Test
	public void find() throws Exception {
		PredicateBuilder builder = predicateBuilderFactory.create();

		SearchOptions searchOptions = searchOptionsFactory.create(builder.equal(pathFactory.create("site.id"), 2001L));

		List<Asset> result = assetService.find(searchOptions);
		Assert.assertEquals(1, result.size());
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();
		Asset expected = new Asset("New Asset with missing product", "ASS-MISS-PRODUCT-001", now, now);
		expected.setProduct(new Product(NON_EXISTENT_LONG_ID));

		assetService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Asset expected = em.getReference(Asset.class, 2003L);
		expected.setProduct(new Product(NON_EXISTENT_LONG_ID));

		assetService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_VendorDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();
		Asset expected = new Asset("New Asset with missing product", "ASS-MISS-VENDOR-001", now, now);
		expected.setVendor(new Vendor(NON_EXISTENT_LONG_ID));

		assetService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_VendorDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Asset expected = em.getReference(Asset.class, 2003L);
		expected.setVendor(new Vendor(NON_EXISTENT_LONG_ID));

		assetService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_SiteDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();
		Asset expected = new Asset("New Asset with missing product", "ASS-MISS-SITE-001", now, now);
		expected.setSite(new Site(NON_EXISTENT_LONG_ID));

		assetService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_SiteDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Asset expected = em.getReference(Asset.class, 2003L);
		expected.setSite(new Site(NON_EXISTENT_LONG_ID));

		assetService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_AssetTypeDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();
		Asset expected = new Asset("New Asset with missing product", "ASS-MISS-ASSETTYPE-001", now, now);
		expected.setAssetType(new AssetType(NON_EXISTENT_LONG_ID));

		assetService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_AssetTypeDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Asset expected = em.getReference(Asset.class, 2003L);
		expected.setAssetType(new AssetType(NON_EXISTENT_LONG_ID));

		assetService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_PurchaseCurrencyDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();
		Asset expected = new Asset("New Asset with missing product", "ASS-MISS-PURCHASECURRENCY-001", now, now);
		expected.setPurchaseCurrency(new Currency(NON_EXISTENT_INT_ID));

		assetService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_PurchaseCurrencyDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Asset expected = em.getReference(Asset.class, 2003L);
		expected.setPurchaseCurrency(new Currency(NON_EXISTENT_INT_ID));

		assetService.update(expected);
	}

}
