/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.service;

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
import org.rainbow.asset.explorer.core.entities.Department;
import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.entities.ProductInventory;
import org.rainbow.asset.explorer.core.entities.ProductIssue;
import org.rainbow.asset.explorer.core.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.core.persistence.dao.InventoryManager;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateProductIssueReferenceNumberException;
import org.rainbow.asset.explorer.core.persistence.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.core.persistence.exceptions.ProductIssueDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/ProductIssueServiceTestSetup.sql")
public class ProductIssueServiceTest extends AbstractServiceTest {

	private static final int NON_EXISTENT_INT_ID = 500000;

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("productIssueService")
	private ProductIssueService productIssueService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("inventoryManager")
	private InventoryManager inventoryManager;

	@Test
	public void create_ProductIssueIsValid_ProductIssueCreated() throws Exception {
		long locationId = 10001L;

		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(locationId);

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-1");
		expected.setLocation(new Location(locationId));
		expected.setDepartment(new Department(10001));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 75);
		detail1.setProduct(new Product(10001L));

		List<ProductIssueDetail> details = new ArrayList<>();
		details.add(detail1);

		expected.setDetails(details);

		Map<Long, Short> productQuantities = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!productQuantities.containsKey(productId)) {
				productQuantities.put(productId, detail.getQuantity());
			} else {
				productQuantities.replace(productId, (short) (productQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productIssueService.create(expected);

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(locationId);

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();

		for (ProductIssueDetail detail : details) {
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
	}

	@Test(expected = ProductIssueDetailsNullOrEmptyException.class)
	public void create_DetailsIsNull_ThrowProductIssueDetailsNullOrEmptyException() throws Exception {
		long locationId = 10001L;

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-DETAILS-NULL");
		expected.setLocation(new Location(locationId));
		expected.setDepartment(new Department(10001));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		productIssueService.create(expected);
	}

	@Test(expected = ProductIssueDetailsNullOrEmptyException.class)
	public void create_DetailsIsEmpty_ThrowProductIssueDetailsNullOrEmptyException() throws Exception {
		long locationId = 10001L;

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-DETAILS-EMPTY");
		expected.setLocation(new Location(locationId));
		expected.setDetails(new ArrayList<ProductIssueDetail>());

		productIssueService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_LocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long locationId = NON_EXISTENT_LONG_ID;

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-LOC-MISSING");
		expected.setLocation(new Location(locationId));
		expected.setDepartment(new Department(10001));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 1800);
		detail1.setProduct(new Product(10001L));

		ProductIssueDetail detail2 = new ProductIssueDetail();
		detail2.setQuantity((short) 1950);
		detail2.setProduct(new Product(10002L));

		List<ProductIssueDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productIssueService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long locationId = 10001L;

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-PRODUCT-MISSING");
		expected.setLocation(new Location(locationId));
		expected.setDepartment(new Department(10001));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 500);
		detail1.setProduct(new Product(NON_EXISTENT_LONG_ID));

		ProductIssueDetail detail2 = new ProductIssueDetail();
		detail2.setQuantity((short) 600);
		detail2.setProduct(new Product(10002L));

		List<ProductIssueDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productIssueService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_DepartmentDoesNotExist_ThrowNonexistentEntityException() throws Exception {

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-DEPT-MISSING");
		expected.setLocation(new Location(10001L));
		expected.setDepartment(new Department(NON_EXISTENT_INT_ID));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 1800);
		detail1.setProduct(new Product(10001L));

		ProductIssueDetail detail2 = new ProductIssueDetail();
		detail2.setQuantity((short) 1950);
		detail2.setProduct(new Product(10002L));

		List<ProductIssueDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		productIssueService.create(expected);
	}

	@Test
	public void update_ProductIssueIsValid_ProductIssueEdited() throws Exception {
		long locationId = 10002L;
		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(locationId);

		long productIssueId = 10001L;
		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		Map<Long, Short> oldProductQuantities = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			oldProductQuantities.put(detail.getProduct().getId(), detail.getQuantity());
			if (detail.getId().getDetailId() == 1) {
				detail.setQuantity((short) 500);
			} else if (detail.getId().getDetailId() == 2) {
				detail.setQuantity((short) 400);
			}
		}

		productIssueService.update(expected);

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(locationId);

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();
		for (ProductIssueDetail detail : details) {
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
					if (ivb.get(productId) + oldProductQuantities.get(productId) - detail.getQuantity() == pia
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
	public void update_NewDetailsAdded_ProductIssueEdited() throws Exception {
		long locationId = 10003L;
		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(locationId);

		Long productIssueId = 10002L;
		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		Map<Long, Short> oldProductQuantities = new HashMap<>();
		Map<Long, Short> newProductQuantities = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!oldProductQuantities.containsKey(productId)) {
				oldProductQuantities.put(productId, detail.getQuantity());
			} else {
				oldProductQuantities.replace(productId,
						(short) (oldProductQuantities.get(productId) + detail.getQuantity()));
			}
			if (detail.getId().getDetailId() == 1) {
				detail.setQuantity((short) 425);
			} else if (detail.getId().getDetailId() == 2) {
				detail.setQuantity((short) 129);
			}
		}

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 500);
		detail1.setProduct(new Product(10001L));
		details.add(detail1);

		ProductIssueDetail detail2 = new ProductIssueDetail();
		detail2.setQuantity((short) 600);
		detail2.setProduct(new Product(10003L));
		details.add(detail2);

		for (ProductIssueDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!newProductQuantities.containsKey(productId)) {
				newProductQuantities.put(productId, detail.getQuantity());
			} else {
				newProductQuantities.replace(productId,
						(short) (newProductQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productIssueService.update(expected);

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(locationId);

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();

		for (ProductIssueDetail detail : details) {
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

					if (ivb.get(productId) + oldQuantity - newQuantity == pia.getQuantity()) {
						matches++;
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());
	}

	@Test(expected = ProductIssueDetailsNullOrEmptyException.class)
	public void update_DetailsIsNull_ThrowProductIssueDetailsNullOrEmptyException() throws Exception {
		ProductIssue expected = em.getReference(ProductIssue.class, 10003L);

		expected.setDetails(null);

		productIssueService.update(expected);
	}

	@Test(expected = ProductIssueDetailsNullOrEmptyException.class)
	public void update_DetailsIsEmpty_ThrowProductIssueDetailsNullOrEmptyException() throws Exception {
		long productIssueId = 10003L;

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		expected.getDetails().clear();

		productIssueService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_LocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long productIssueId = 10004L;

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		expected.setLocation(new Location(NON_EXISTENT_LONG_ID));

		productIssueService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_DepartmentDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		long productIssueId = 10012L;

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		expected.setDepartment(new Department(NON_EXISTENT_INT_ID));

		productIssueService.update(expected);
	}

	@Test(expected = InsufficientInventoryException.class)
	public void update_NewQuantityGreaterThanInventoryQuantity_ThrowInsufficientInventoryException() throws Exception {
		Long locationId = 10006L;
		Long productIssueId = 10005L;
		Short availableQuantity = 2000;
		Short requestedQuantity = 2700;
		Long productId = 10001L;

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		Map<Long, Short> oldProductQuantities = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			oldProductQuantities.put(detail.getProduct().getId(), detail.getQuantity());
			if (detail.getId().getDetailId() == 1) {
				detail.setQuantity((short) 5000);
			}
		}
		try {
			productIssueService.update(expected);
		} catch (InsufficientInventoryException e) {
			Assert.assertEquals(locationId, e.getLocationId());
			Assert.assertEquals(availableQuantity, e.getAvailableQuantity());
			Assert.assertEquals(requestedQuantity, e.getRequestedQuantity());
			Assert.assertEquals(productId, e.getProductId());
			throw e;
		}
	}

	@Test(expected = InsufficientInventoryException.class)
	public void update_NewQuantityNotInInventory_ThrowInsufficientInventoryException() throws Exception {
		Long locationId = 10007L;
		Long productIssueId = 10006L;
		Short availableQuantity = 0;
		Short requestedQuantity = 400;
		Long productId = 10003L;

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity(requestedQuantity);
		detail1.setProduct(new Product(productId));
		details.add(detail1);

		try {
			productIssueService.update(expected);
		} catch (InsufficientInventoryException e) {
			Assert.assertEquals(locationId, e.getLocationId());
			Assert.assertEquals(availableQuantity, e.getAvailableQuantity());
			Assert.assertEquals(requestedQuantity, e.getRequestedQuantity());
			Assert.assertEquals(productId, e.getProductId());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ProductChangedToNonexistentProduct_ThrowNonexistentEntityException() throws Exception {
		long productIssueId = 10007L;

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		expected.getDetails().get(0).setProduct(new Product(NON_EXISTENT_LONG_ID));

		productIssueService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_NonexistentProductAddedInDetails_ThrowNonexistentEntityException() throws Exception {
		long productIssueId = 10007L;

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 400);
		detail1.setProduct(new Product(NON_EXISTENT_LONG_ID));
		expected.getDetails().add(detail1);

		productIssueService.update(expected);
	}

	@Test
	public void update_LocationChanged_OldLocationAndNewLocationInventoriesAdjusted() throws Exception {
		long oldLocationId = 10009L;
		long newLocationId = 10010L;
		List<ProductInventory> oldLocationInventoryBefore = inventoryManager.getProductInventories(oldLocationId);
		List<ProductInventory> newLocationInventoryBefore = inventoryManager.getProductInventories(newLocationId);

		long productIssueId = 10008L;
		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		expected.setLocation(new Location(newLocationId));

		Map<Long, Short> oldProductQuantities = new HashMap<>();
		Map<Long, Short> newProductQuantities = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!oldProductQuantities.containsKey(productId)) {
				oldProductQuantities.put(productId, detail.getQuantity());
			} else {
				oldProductQuantities.replace(productId,
						(short) (oldProductQuantities.get(productId) + detail.getQuantity()));
			}
			if (detail.getId().getDetailId() == 1) {
				detail.setQuantity((short) 400);
			} else if (detail.getId().getDetailId() == 2) {
				detail.setQuantity((short) 275);
			}
			if (!newProductQuantities.containsKey(productId)) {
				newProductQuantities.put(productId, detail.getQuantity());
			} else {
				newProductQuantities.replace(productId,
						(short) (newProductQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productIssueService.update(expected);

		List<ProductInventory> oldLocationInventoryAfter = inventoryManager.getProductInventories(oldLocationId);
		List<ProductInventory> newLocationInventoryAfter = inventoryManager.getProductInventories(newLocationId);

		int oldLocationInventoryMatches = 0;
		int newLocationInventoryMatches = 0;

		Map<Long, Short> ivb = new HashMap<>();
		Map<Long, Short> ivb1 = new HashMap<>();

		for (ProductIssueDetail detail : details) {
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
						if (ivb.get(productId) + oldProductQuantities.get(productId) == pia.getQuantity()) {
							oldLocationInventoryMatches++;
							oldProductQuantities.remove(productId);
						}
					}
				}
			}
			for (ProductInventory pia : newLocationInventoryAfter) {
				if (productId.equals(pia.getId().getProductId())) {
					if (newProductQuantities.containsKey(productId)) {
						if (ivb1.get(productId) - newProductQuantities.get(productId) == pia.getQuantity()) {
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
	public void delete_ProductIssueExists_ProductIssueDeletedAndLocationInventoryAdjusted() throws Exception {
		long locationId = 10011L;
		long productIssueId = 10010L;

		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(locationId);

		ProductIssue expected = em.getReference(ProductIssue.class, productIssueId);
		List<ProductIssueDetail> details = productIssueService.getDetails(productIssueId);
		expected.setDetails(details);

		Map<Long, Short> productQuantities = new HashMap<>();

		for (ProductIssueDetail detail : details) {
			Long productId = detail.getProduct().getId();
			if (!productQuantities.containsKey(productId)) {
				productQuantities.put(productId, detail.getQuantity());
			} else {
				productQuantities.replace(productId, (short) (productQuantities.get(productId) + detail.getQuantity()));
			}
		}

		productIssueService.delete(expected);

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(locationId);

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();

		for (ProductIssueDetail detail : details) {
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
						if (ivb.get(productId) + productQuantities.get(productId) == pia.getQuantity()) {
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
			em.getReference(ProductIssue.class, productIssueId);
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ProductIssueDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final ProductIssue productIssue = new ProductIssue(NON_EXISTENT_LONG_ID);
		productIssueService.delete(productIssue);
	}

	@Test(expected = InsufficientInventoryException.class)
	public void create_LocationDoesNotHaveSufficientInventory_ThrowInsufficientInventoryException() throws Exception {
		Long locationId = 10012L;
		Short availableQuantity = 50;
		Short requestedQuantity = 75;
		Long productId = 10001L;

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-INS-INV");
		expected.setLocation(new Location(locationId));
		expected.setDepartment(new Department(10001));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity(requestedQuantity);
		detail1.setProduct(new Product(productId));

		List<ProductIssueDetail> details = new ArrayList<>();
		details.add(detail1);

		expected.setDetails(details);

		try {
			productIssueService.create(expected);
		} catch (InsufficientInventoryException e) {
			Assert.assertEquals(locationId, e.getLocationId());
			Assert.assertEquals(availableQuantity, e.getAvailableQuantity());
			Assert.assertEquals(requestedQuantity, e.getRequestedQuantity());
			Assert.assertEquals(productId, e.getProductId());
			throw e;
		}
	}

	@Test(expected = DuplicateProductIssueReferenceNumberException.class)
	public void create_ReferenceNumberAlreadyExists_ThrowDuplicateProductIssueReferenceNumberException()
			throws Exception {
		long locationId = 10001L;

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("PI-10011");
		expected.setLocation(new Location(locationId));
		expected.setDepartment(new Department(10001));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 75);
		detail1.setProduct(new Product(10001L));

		List<ProductIssueDetail> details = new ArrayList<>();
		details.add(detail1);

		expected.setDetails(details);

		try {
			productIssueService.create(expected);
		} catch (DuplicateProductIssueReferenceNumberException e) {
			Assert.assertEquals(expected.getReferenceNumber(), e.getReferenceNumber());
			throw e;
		}
	}
}
