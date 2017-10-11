package org.rainbow.asset.explorer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.orm.entities.ProductReceipt;
import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetail;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductReceiptReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.service.exceptions.ProductReceiptDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.service.services.InventoryManager;
import org.rainbow.asset.explorer.service.services.ProductInventoryService;
import org.rainbow.asset.explorer.service.services.ProductReceiptService;
import org.rainbow.asset.explorer.util.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/ProductReceiptServiceTestSetup.sql")
public class ProductReceiptServiceTest extends AbstractServiceTest {

	private static final int NON_EXISTENT_INT_ID = 500000;

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("productReceiptService")
	private ProductReceiptService productReceiptService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("inventoryManager")
	private InventoryManager inventoryManager;

	@Autowired
	private ProductInventoryService productInventoryService;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Test
	public void create_ProductReceiptIsValid_ProductReceiptCreated() throws Exception {
		long locationId = 11001L;
		List<ProductInventory> inventoryBefore = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("NEW-PR-1");
		expected.setLocation(new Location(locationId));
		expected.setVendor(new Vendor(11001L));
		expected.setCurrency(new Currency(11001));
		expected.setReceiptDate(new Date());

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 2500);
		detail1.setProduct(new Product(11001L));
		detail1.setUnitPrice(5D);

		ProductReceiptDetail detail2 = new ProductReceiptDetail();
		detail2.setQuantity((short) 3000);
		detail2.setProduct(new Product(11002L));
		detail2.setUnitPrice(3.75D);

		List<ProductReceiptDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productReceiptService.create(expected);

		ProductReceipt actual = em.getReference(ProductReceipt.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.

		List<ProductInventory> inventoryAfter = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();
		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!ivb.containsKey(productId)) {
				ivb.put(productId, (short) 0);
			}
			for (ProductInventory pib : inventoryBefore) {
				if (productId.equals(pib.getId().getProductId())) {
					ivb.replace(productId, (short) (ivb.get(productId) + pib.getQuantity()));
				}
			}
			for (ProductInventory pia : inventoryAfter) {
				if (productId.equals(pia.getId().getProductId())) {
					if (ivb.get(productId) + detail.getQuantity() == pia.getQuantity()) {
						matches++;
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());
	}

	@Test(expected = ProductReceiptDetailsNullOrEmptyException.class)
	public void create_DetailsIsNull_ThrowProductReceiptDetailsNullOrEmptyException() throws Exception {
		long locationId = 11001L;

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("NEW-PR-DETAILS-NULL");
		expected.setLocation(new Location(locationId));
		expected.setVendor(new Vendor(11001L));
		expected.setCurrency(new Currency(11001));
		expected.setReceiptDate(new Date());

		productReceiptService.create(expected);
	}

	@Test(expected = ProductReceiptDetailsNullOrEmptyException.class)
	public void create_DetailsIsEmpty_ThrowProductReceiptDetailsNullOrEmptyException() throws Exception {
		long locationId = 11001L;

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("NEW-PR-DETAILS-EMPTY");
		expected.setLocation(new Location(locationId));
		expected.setDetails(new ArrayList<ProductReceiptDetail>());
		expected.setVendor(new Vendor(11001L));
		expected.setCurrency(new Currency(11001));
		expected.setReceiptDate(new Date());

		productReceiptService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_LocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long locationId = NON_EXISTENT_LONG_ID;

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("NEW-PR-LOC-MISSING");
		expected.setLocation(new Location(locationId));
		expected.setVendor(new Vendor(11001L));
		expected.setCurrency(new Currency(11001));
		expected.setReceiptDate(new Date());

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 2500);
		detail1.setProduct(new Product(11001L));

		ProductReceiptDetail detail2 = new ProductReceiptDetail();
		detail2.setQuantity((short) 3000);
		detail2.setProduct(new Product(11002L));

		List<ProductReceiptDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productReceiptService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long locationId = 11001L;

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("NEW-PR-PRODUCT-MISSING");
		expected.setLocation(new Location(locationId));
		expected.setVendor(new Vendor(11001L));
		expected.setCurrency(new Currency(11001));
		expected.setReceiptDate(new Date());

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 2500);
		detail1.setProduct(new Product(NON_EXISTENT_LONG_ID));

		ProductReceiptDetail detail2 = new ProductReceiptDetail();
		detail2.setQuantity((short) 3000);
		detail2.setProduct(new Product(11002L));

		List<ProductReceiptDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productReceiptService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_VendorDoesNotExist_ThrowNonexistentEntityException() throws Exception {

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("NEW-PR-VENDOR-MISSING");
		expected.setLocation(new Location(11001L));
		expected.setVendor(new Vendor(NON_EXISTENT_LONG_ID));
		expected.setCurrency(new Currency(11001));
		expected.setReceiptDate(new Date());

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 2500);
		detail1.setProduct(new Product(11001L));

		ProductReceiptDetail detail2 = new ProductReceiptDetail();
		detail2.setQuantity((short) 3000);
		detail2.setProduct(new Product(11002L));

		List<ProductReceiptDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productReceiptService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_CurrencyDoesNotExist_ThrowNonexistentEntityException() throws Exception {

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("NEW-PR-CURRENCY-MISSING");
		expected.setLocation(new Location(11001L));
		expected.setVendor(new Vendor(11001L));
		expected.setCurrency(new Currency(NON_EXISTENT_INT_ID));
		expected.setReceiptDate(new Date());

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 2500);
		detail1.setProduct(new Product(11001L));

		ProductReceiptDetail detail2 = new ProductReceiptDetail();
		detail2.setQuantity((short) 3000);
		detail2.setProduct(new Product(11002L));

		List<ProductReceiptDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productReceiptService.create(expected);
	}

	@Test
	public void update_ProductReceiptIsValid_ProductReceiptEdited() throws Exception {
		long locationId = 11002L;
		List<ProductInventory> inventoryBefore = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		long productReceiptId = 11002L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		Map<Long, Short> oldProductQuantities = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			oldProductQuantities.put(detail.getProduct().getId(), detail.getQuantity());
			if (detail.getId().getDetailId() == 11001) {
				detail.setQuantity((short) 1500);
			} else if (detail.getId().getDetailId() == 11002) {
				detail.setQuantity((short) 1750);
			}
		}

		productReceiptService.update(expected);

		List<ProductInventory> inventoryAfter = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();
		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!ivb.containsKey(productId)) {
				ivb.put(productId, (short) 0);
			}
			for (ProductInventory pib : inventoryBefore) {
				if (productId.equals(pib.getId().getProductId())) {
					ivb.replace(productId, (short) (ivb.get(productId) + pib.getQuantity()));
				}
			}
			for (ProductInventory pia : inventoryAfter) {
				if (productId.equals(pia.getId().getProductId())) {
					if (ivb.get(productId) - oldProductQuantities.get(productId) + detail.getQuantity() == pia
							.getQuantity()) {
						matches++;
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());
	}

	@Test
	public void update_NewDetailsAdded_ProductReceiptEdited() throws Exception {
		long locationId = 11003L;
		List<ProductInventory> inventoryBefore = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		long productReceiptId = 11003L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		Map<Long, Short> oldProductQuantities = new HashMap<>();
		Map<Long, Short> newProductQuantities = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!oldProductQuantities.containsKey(productId)) {
				oldProductQuantities.put(productId, detail.getQuantity());
			} else {
				oldProductQuantities.replace(productId,
						(short) (oldProductQuantities.get(productId) + detail.getQuantity()));
			}
			if (detail.getId().getDetailId() == 11003) {
				detail.setQuantity((short) 425);
			} else if (detail.getId().getDetailId() == 11004) {
				detail.setQuantity((short) 129);
			}
		}

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 500);
		detail1.setProduct(new Product(11001L));
		detail1.setUnitPrice(5D);
		details.add(detail1);

		ProductReceiptDetail detail2 = new ProductReceiptDetail();
		detail2.setQuantity((short) 600);
		detail2.setProduct(new Product(11003L));
		detail2.setUnitPrice(4D);
		details.add(detail2);

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!newProductQuantities.containsKey(productId)) {
				newProductQuantities.put(productId, detail.getQuantity());
			} else {
				newProductQuantities.replace(productId,
						(short) (newProductQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productReceiptService.update(expected);

		List<ProductInventory> inventoryAfter = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();

			if (!ivb.containsKey(productId)) {
				ivb.put(productId, (short) 0);
			}
			for (ProductInventory pib : inventoryBefore) {
				if (productId.equals(pib.getId().getProductId())) {
					ivb.replace(productId, (short) (ivb.get(productId) + pib.getQuantity()));
				}
			}

			for (ProductInventory pia : inventoryAfter) {
				if (productId.equals(pia.getId().getProductId())) {

					short newQuantity = newProductQuantities.containsKey(productId)
							? newProductQuantities.get(productId) : 0;
					short oldQuantity = oldProductQuantities.containsKey(productId)
							? oldProductQuantities.get(productId) : 0;

					if (ivb.get(productId) - oldQuantity + newQuantity == pia.getQuantity()) {
						matches++;
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());
	}

	@Test(expected = ProductReceiptDetailsNullOrEmptyException.class)
	public void update_DetailsIsNull_ThrowProductReceiptDetailsNullOrEmptyException() throws Exception {
		ProductReceipt expected = em.getReference(ProductReceipt.class, 11004L);

		expected.setDetails(null);

		productReceiptService.update(expected);
	}

	@Test(expected = ProductReceiptDetailsNullOrEmptyException.class)
	public void update_DetailsIsEmpty_ThrowProductReceiptDetailsNullOrEmptyException() throws Exception {
		long productReceiptId = 11004L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		expected.getDetails().clear();

		productReceiptService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_LocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long productReceiptId = 11005L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		expected.setLocation(new Location(NON_EXISTENT_LONG_ID));

		productReceiptService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_VendorDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long productReceiptId = 11013L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		expected.setVendor(new Vendor(NON_EXISTENT_LONG_ID));

		productReceiptService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_CurrencyDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long productReceiptId = 11014L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		expected.setCurrency(new Currency(NON_EXISTENT_INT_ID));

		productReceiptService.update(expected);
	}

	@Test(expected = InsufficientInventoryException.class)
	public void update_OldQuantityMinusNewQuantityGreaterThanInventoryQuantity_ThrowInsufficientInventoryException()
			throws Exception {
		Long locationId = 11006L;
		Long productReceiptId = 11006L;
		Short availableQuantity = 2000;
		Short requestedQuantity = 2200;
		Long productId = 11001L;

		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		for (ProductReceiptDetail detail : details) {
			if (detail.getId().getDetailId() == 11009) {
				detail.setQuantity((short) 100);
			}
		}
		try {
			productReceiptService.update(expected);
		} catch (InsufficientInventoryException e) {
			Assert.assertEquals(locationId, e.getLocationId());
			Assert.assertEquals(availableQuantity, e.getAvailableQuantity());
			Assert.assertEquals(requestedQuantity, e.getRequestedQuantity());
			Assert.assertEquals(productId, e.getProductId());
			throw e;
		}
	}

	@Test
	public void update_PreviouselyReceivedQuantityIsUsedUpAndNewQuantityIsGreaterThanOldQuantity_NewQuantityMinusOldQuantityAddedToInventory()
			throws Exception {
		Long locationId = 11007L;
		Long productReceiptId = 11007L;

		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		List<ProductInventory> inventoryBefore = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		Map<Long, Short> oldProductQuantities = new HashMap<>();
		Map<Long, Short> newProductQuantities = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!oldProductQuantities.containsKey(productId)) {
				oldProductQuantities.put(productId, detail.getQuantity());
			} else {
				oldProductQuantities.replace(productId,
						(short) (oldProductQuantities.get(productId) + detail.getQuantity()));
			}
			// We want to modify detail 3. Since the available quantity of this
			// product in the inventory is 0 and 400 were received before, it
			// means those 400 were used. Changing the quantity to 2000 should
			// therefore add 1600 to the inventory.
			if (detail.getId().getDetailId() == 11011 && detail.getProduct().getId().equals(11003L)) {
				detail.setQuantity((short) 2000);
				// break;
			}
			if (!newProductQuantities.containsKey(productId)) {
				newProductQuantities.put(productId, detail.getQuantity());
			} else {
				newProductQuantities.replace(productId,
						(short) (newProductQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productReceiptService.update(expected);

		List<ProductInventory> inventoryAfter = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();
		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!ivb.containsKey(productId)) {
				ivb.put(productId, (short) 0);
				for (ProductInventory pib : inventoryBefore) {
					if (productId.equals(pib.getId().getProductId())) {
						ivb.replace(productId, (short) (ivb.get(productId) + pib.getQuantity()));
					}
				}
				for (ProductInventory pia : inventoryAfter) {
					if (productId.equals(pia.getId().getProductId())) {
						if (ivb.get(productId) - oldProductQuantities.get(productId)
								+ newProductQuantities.get(productId) == pia.getQuantity()) {
							matches++;
						}
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ProductChangedToNonexistentProduct_ThrowNonexistentEntityException() throws Exception {
		long productReceiptId = 11008L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		expected.getDetails().get(0).setProduct(new Product(NON_EXISTENT_LONG_ID));

		productReceiptService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_NonexistentProductAddedInDetails_ThrowNonexistentEntityException() throws Exception {
		long productReceiptId = 11008L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 2000);
		detail1.setProduct(new Product(NON_EXISTENT_LONG_ID));
		expected.getDetails().add(detail1);

		productReceiptService.update(expected);
	}

	@Test
	public void update_LocationChanged_OldLocationAndNewLocationInventoriesAdjusted() throws Exception {
		long oldLocationId = 11009L;
		long newLocationId = 11010L;
		List<ProductInventory> oldLocationInventoryBefore = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), oldLocationId)));
		List<ProductInventory> newLocationInventoryBefore = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), newLocationId)));

		long productReceiptId = 11009L;
		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		expected.setLocation(new Location(newLocationId));

		Map<Long, Short> oldProductQuantities = new HashMap<>();
		Map<Long, Short> newProductQuantities = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!oldProductQuantities.containsKey(productId)) {
				oldProductQuantities.put(productId, detail.getQuantity());
			} else {
				oldProductQuantities.replace(productId,
						(short) (oldProductQuantities.get(productId) + detail.getQuantity()));
			}
			if (detail.getId().getDetailId() == 11016) {
				detail.setQuantity((short) 400);
			} else if (detail.getId().getDetailId() == 11017) {
				detail.setQuantity((short) 275);
			}
			if (!newProductQuantities.containsKey(productId)) {
				newProductQuantities.put(productId, detail.getQuantity());
			} else {
				newProductQuantities.replace(productId,
						(short) (newProductQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productReceiptService.update(expected);

		List<ProductInventory> oldLocationInventoryAfter = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), oldLocationId)));
		List<ProductInventory> newLocationInventoryAfter = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), newLocationId)));

		int oldLocationInventoryMatches = 0;
		int newLocationInventoryMatches = 0;

		Map<Long, Short> ivb = new HashMap<>();
		Map<Long, Short> ivb1 = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!ivb.containsKey(productId)) {
				ivb.put(productId, (short) 0);
			}
			if (!ivb1.containsKey(productId)) {
				ivb1.put(productId, (short) 0);
			}
			for (ProductInventory pib : oldLocationInventoryBefore) {
				if (productId.equals(pib.getId().getProductId())) {
					ivb.replace(productId, (short) (ivb.get(productId) + pib.getQuantity()));
				}
			}
			for (ProductInventory pib : newLocationInventoryBefore) {
				if (productId.equals(pib.getId().getProductId())) {
					ivb1.replace(productId, (short) (ivb1.get(productId) + pib.getQuantity()));
				}
			}
			for (ProductInventory pia : oldLocationInventoryAfter) {
				if (productId.equals(pia.getId().getProductId())) {
					if (oldProductQuantities.containsKey(productId)) {
						if (ivb.get(productId) - oldProductQuantities.get(productId) == pia.getQuantity()) {
							oldLocationInventoryMatches++;
							oldProductQuantities.remove(productId);
						}
					}
				}
			}
			for (ProductInventory pia : newLocationInventoryAfter) {
				if (productId.equals(pia.getId().getProductId())) {
					if (newProductQuantities.containsKey(productId)) {
						if (ivb1.get(productId) + newProductQuantities.get(productId) == pia.getQuantity()) {
							newLocationInventoryMatches++;
							newProductQuantities.remove(productId);
						}
					}
				}
			}
		}
		Assert.assertTrue(oldLocationInventoryMatches > 0);
		Assert.assertEquals(oldLocationInventoryMatches, oldLocationInventoryAfter.size());
		Assert.assertTrue(newLocationInventoryMatches > 0);
		Assert.assertEquals(newLocationInventoryMatches, newLocationInventoryAfter.size());
	}

	@Test
	public void delete_ProductReceiptExists_ProductReceiptDeletedAndLocationInventoryAdjusted() throws Exception {
		long locationId = 11011L;
		long productReceiptId = 11010L;

		List<ProductInventory> inventoryBefore = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		ProductReceipt expected = em.getReference(ProductReceipt.class, productReceiptId);
		List<ProductReceiptDetail> details = productReceiptService.getDetails(productReceiptId);
		expected.setDetails(details);

		Map<Long, Short> productQuantities = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!productQuantities.containsKey(productId)) {
				productQuantities.put(productId, detail.getQuantity());
			} else {
				productQuantities.replace(productId, (short) (productQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productReceiptService.delete(expected);

		List<ProductInventory> inventoryAfter = productInventoryService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("id.locationId"), locationId)));

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();

		for (ProductReceiptDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!ivb.containsKey(productId)) {
				ivb.put(productId, (short) 0);
			}
			for (ProductInventory pib : inventoryBefore) {
				if (productId.equals(pib.getId().getProductId())) {
					ivb.replace(productId, (short) (ivb.get(productId) + pib.getQuantity()));
				}
			}
			for (ProductInventory pia : inventoryAfter) {
				if (productId.equals(pia.getId().getProductId())) {
					if (productQuantities.containsKey(productId)) {
						if (ivb.get(productId) - productQuantities.get(productId) == pia.getQuantity()) {
							matches++;
							productQuantities.remove(productId);
						}
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());

		em.clear();

		boolean deleted = false;
		try {
			em.getReference(ProductReceipt.class, productReceiptId);
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ProductReceiptDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final ProductReceipt productReceipt = new ProductReceipt(NON_EXISTENT_LONG_ID);
		productReceiptService.delete(productReceipt);
	}

	@Test(expected = InsufficientInventoryException.class)
	public void delete_LocationDoesNotHaveSufficientInventory_ThrowInsufficientInventoryException() throws Exception {
		final ProductReceipt productReceipt = new ProductReceipt(11011L);
		Long locationId = 11012L;
		Short availableQuantity = 0;
		Short requestedQuantity = 200;
		Long productId = 11001L;

		try {
			productReceiptService.delete(productReceipt);
		} catch (InsufficientInventoryException e) {
			Assert.assertEquals(locationId, e.getLocationId());
			Assert.assertEquals(availableQuantity, e.getAvailableQuantity());
			Assert.assertEquals(requestedQuantity, e.getRequestedQuantity());
			Assert.assertEquals(productId, e.getProductId());
			throw e;
		}
	}

	@Test(expected = DuplicateProductReceiptReferenceNumberException.class)
	public void create_ReferenceNumberAlreadyExists_ThrowDuplicateProductReceiptReferenceNumberException()
			throws Exception {
		long locationId = 11013L;

		ProductReceipt expected = new ProductReceipt();
		expected.setReferenceNumber("PR-11012");
		expected.setLocation(new Location(locationId));
		expected.setVendor(new Vendor(11001L));
		expected.setCurrency(new Currency(11001));
		expected.setReceiptDate(new Date());

		ProductReceiptDetail detail1 = new ProductReceiptDetail();
		detail1.setQuantity((short) 2500);
		detail1.setProduct(new Product(11001L));

		ProductReceiptDetail detail2 = new ProductReceiptDetail();
		detail2.setQuantity((short) 3000);
		detail2.setProduct(new Product(11002L));

		List<ProductReceiptDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		try {
			productReceiptService.create(expected);
		} catch (DuplicateProductReceiptReferenceNumberException e) {
			Assert.assertEquals(expected.getReferenceNumber(), e.getReferenceNumber());
			throw e;
		}
	}
}
