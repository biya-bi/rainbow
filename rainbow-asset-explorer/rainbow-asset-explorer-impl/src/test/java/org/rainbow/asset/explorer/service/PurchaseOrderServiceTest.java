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
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrder;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetail;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderStatus;
import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.persistence.dao.InventoryManager;
import org.rainbow.asset.explorer.service.exceptions.DuplicatePurchaseOrderReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderCompleteQuantityOutOfRangeException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderReadOnlyException;
import org.rainbow.asset.explorer.service.exceptions.PurchaseOrderStatusTransitionException;
import org.rainbow.asset.explorer.service.exceptions.VendorInactiveException;
import org.rainbow.asset.explorer.service.services.PurchaseOrderService;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/PurchaseOrderServiceTestSetup.sql")
public class PurchaseOrderServiceTest extends AbstractServiceTest {

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("purchaseOrderService")
	private PurchaseOrderService purchaseOrderService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("inventoryManager")
	private InventoryManager inventoryManager;

	@Test
	public void create_PurchaseOrderIsValid_PurchaseOrderCreated() throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("NEW-PO-REF");
		expected.setVendor(new Vendor(13001L));
		expected.setShipMethod(new ShipMethod(13001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		PurchaseOrderDetail detail1 = new PurchaseOrderDetail();
		detail1.setOrderedQuantity((short) 2500);
		detail1.setUnitPrice(10D);
		detail1.setProduct(new Product(13001L));
		detail1.setDueDate(now);

		PurchaseOrderDetail detail2 = new PurchaseOrderDetail();
		detail2.setOrderedQuantity((short) 3000);
		detail2.setUnitPrice(25D);
		detail2.setProduct(new Product(13002L));
		detail2.setDueDate(now);

		List<PurchaseOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		purchaseOrderService.create(expected);

		em.clear();

		PurchaseOrder actual = em.getReference(PurchaseOrder.class, expected.getId());
		actual.setDetails(purchaseOrderService.getDetails(actual.getId()));

		actual.getId(); // If no exception is thrown, then the create was
						// successful.
		Assert.assertEquals(PurchaseOrderStatus.PENDING, actual.getStatus());
		Assert.assertEquals(expected.getDetails().size(), actual.getDetails().size());
	}

	@Test(expected = DuplicatePurchaseOrderReferenceNumberException.class)
	public void create_ReferenceNumberAlreadyExists_ThrowDuplicatePurchaseOrderReferenceNumberException()
			throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("PO-13001");
		expected.setVendor(new Vendor(13001L));
		expected.setShipMethod(new ShipMethod(13001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		PurchaseOrderDetail detail1 = new PurchaseOrderDetail();
		detail1.setOrderedQuantity((short) 2500);
		detail1.setUnitPrice(10D);
		detail1.setProduct(new Product(13001L));
		detail1.setDueDate(now);

		PurchaseOrderDetail detail2 = new PurchaseOrderDetail();
		detail2.setOrderedQuantity((short) 3000);
		detail2.setUnitPrice(25D);
		detail2.setProduct(new Product(13002L));
		detail2.setDueDate(now);

		List<PurchaseOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		try {
			purchaseOrderService.create(expected);
		} catch (DuplicatePurchaseOrderReferenceNumberException e) {
			Assert.assertEquals(expected.getReferenceNumber(), e.getReferenceNumber());
			throw e;
		}
	}

	@Test(expected = DuplicatePurchaseOrderReferenceNumberException.class)
	public void update_ReferenceNumberAlreadyExists_ThrowDuplicatePurchaseOrderReferenceNumberException()
			throws Exception {
		Long purchaseOrderId = 13003L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		expected.setReferenceNumber("PO-13002");

		try {
			purchaseOrderService.update(expected);
		} catch (DuplicatePurchaseOrderReferenceNumberException e) {
			Assert.assertEquals(expected.getReferenceNumber(), e.getReferenceNumber());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_VendorDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("PO-5000");
		expected.setVendor(new Vendor(NON_EXISTENT_LONG_ID));
		expected.setShipMethod(new ShipMethod(13001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		PurchaseOrderDetail detail1 = new PurchaseOrderDetail();
		detail1.setOrderedQuantity((short) 2500);
		detail1.setUnitPrice(10D);
		detail1.setProduct(new Product(13001L));
		detail1.setDueDate(now);

		PurchaseOrderDetail detail2 = new PurchaseOrderDetail();
		detail2.setOrderedQuantity((short) 3000);
		detail2.setUnitPrice(25D);
		detail2.setProduct(new Product(13002L));
		detail2.setDueDate(now);

		List<PurchaseOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		purchaseOrderService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_VendorDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long purchaseOrderId = 13004L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		expected.setVendor(new Vendor(NON_EXISTENT_LONG_ID));

		purchaseOrderService.update(expected);
	}

	@Test(expected = VendorInactiveException.class)
	public void create_VendorIsInactive_ThrowVendorInactiveException() throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("PO-VDR-INA-13002");
		expected.setVendor(new Vendor(13002L));
		expected.setShipMethod(new ShipMethod(13001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		PurchaseOrderDetail detail1 = new PurchaseOrderDetail();
		detail1.setOrderedQuantity((short) 2500);
		detail1.setUnitPrice(10D);
		detail1.setProduct(new Product(13001L));
		detail1.setDueDate(now);

		PurchaseOrderDetail detail2 = new PurchaseOrderDetail();
		detail2.setOrderedQuantity((short) 3000);
		detail2.setUnitPrice(25D);
		detail2.setProduct(new Product(13002L));
		detail2.setDueDate(now);

		List<PurchaseOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		purchaseOrderService.create(expected);
	}

	@Test(expected = VendorInactiveException.class)
	public void update_VendorIsInactive_ThrowVendorInactiveException() throws Exception {
		Long purchaseOrderId = 13005L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		expected.setVendor(new Vendor(13002L));

		purchaseOrderService.update(expected);
	}

	@Test
	public void approve_PurchaseOrderIsPending_PurchaseOrderApproved() throws Exception {
		Long id = 13006L;

		purchaseOrderService.approve(new PurchaseOrder(id));

		em.clear();
		PurchaseOrder actual = em.getReference(PurchaseOrder.class, id);
		Assert.assertEquals(PurchaseOrderStatus.APPROVED, actual.getStatus());
	}

	@Test(expected = NonexistentEntityException.class)
	public void approve_PurchaseOrderDoesNotExist_ThrowNonexistentEntityExceptionException() throws Exception {
		purchaseOrderService.approve(new PurchaseOrder(NON_EXISTENT_LONG_ID));
	}

	@Test
	public void reject_PurchaseOrderIsPending_PurchaseOrderRejected() throws Exception {
		Long id = 13007L;

		purchaseOrderService.reject(new PurchaseOrder(id));

		em.clear();
		PurchaseOrder actual = em.getReference(PurchaseOrder.class, id);
		Assert.assertEquals(PurchaseOrderStatus.REJECTED, actual.getStatus());
	}

	@Test(expected = NonexistentEntityException.class)
	public void reject_PurchaseOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		purchaseOrderService.reject(new PurchaseOrder(NON_EXISTENT_LONG_ID));
	}

	@Test
	public void complete_PurchaseOrderIsApproved_PurchaseOrderComplete() throws Exception {
		Long purchaseOrderId = 13008L;
		Long locationId = 13001L;

		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(locationId);

		Map<Long, Short> productCount = new HashMap<>();
		productCount.put(13001L, (short) 1000);
		productCount.put(13003L, (short) 500);
		purchaseOrderService.complete(new PurchaseOrder(purchaseOrderId), new Location(locationId), productCount);

		PurchaseOrder actual = em.getReference(PurchaseOrder.class, purchaseOrderId);
		Assert.assertEquals(PurchaseOrderStatus.COMPLETE, actual.getStatus());

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(locationId);

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();

		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);

		for (PurchaseOrderDetail detail : details) {
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
					if (ivb.get(productId) + detail.getReceivedQuantity() == pia.getQuantity() && detail
							.getOrderedQuantity() == detail.getReceivedQuantity() + detail.getRejectedQuantity()) {
						matches++;
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());
	}

	@Test(expected = NonexistentEntityException.class)
	public void complete_PurchaseOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long purchaseOrderId = NON_EXISTENT_LONG_ID;

		Map<Long, Short> productCount = new HashMap<>();
		productCount.put(13001L, (short) 1000);
		purchaseOrderService.complete(new PurchaseOrder(purchaseOrderId), new Location(13001L), productCount);
	}

	@Test(expected = PurchaseOrderStatusTransitionException.class)
	public void complete_PurchaseOrderIsNotApproved_ThrowPurchaseOrderStatusTransitionException() throws Exception {
		Long id = 13009L;

		try {
			Map<Long, Short> productCount = new HashMap<>();
			productCount.put(13001L, (short) 1000);
			purchaseOrderService.complete(new PurchaseOrder(id), new Location(13001L), productCount);
		} catch (PurchaseOrderStatusTransitionException e) {
			Assert.assertEquals(PurchaseOrderStatus.PENDING, e.getSourceStatus());
			Assert.assertEquals(PurchaseOrderStatus.COMPLETE, e.getTargetStatus());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void complete_LocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long locationId = NON_EXISTENT_LONG_ID;
		Long purchaseOrderId = 13010L;

		Map<Long, Short> productCount = new HashMap<>();
		productCount.put(13001L, (short) 1000);
		purchaseOrderService.complete(new PurchaseOrder(purchaseOrderId), new Location(locationId), productCount);
	}

	@Test
	public void delete_PurchaseOrderExists_PurchaseOrderDeleted() throws Exception {
		final PurchaseOrder purchaseOrder = new PurchaseOrder(13011L);
		boolean deleted = false;

		purchaseOrderService.delete(purchaseOrder);

		try {
			em.getReference(PurchaseOrder.class, purchaseOrder.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_PurchaseOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final PurchaseOrder purchaseOrder = new PurchaseOrder(NON_EXISTENT_LONG_ID);
		purchaseOrderService.delete(purchaseOrder);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ShipMethodDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("PO-SHM-MIS");
		expected.setVendor(new Vendor(13001L));
		expected.setShipMethod(new ShipMethod(NON_EXISTENT_LONG_ID));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		PurchaseOrderDetail detail1 = new PurchaseOrderDetail();
		detail1.setOrderedQuantity((short) 2500);
		detail1.setUnitPrice(10D);
		detail1.setProduct(new Product(13001L));
		detail1.setDueDate(now);

		PurchaseOrderDetail detail2 = new PurchaseOrderDetail();
		detail2.setOrderedQuantity((short) 3000);
		detail2.setUnitPrice(25D);
		detail2.setProduct(new Product(13002L));
		detail2.setDueDate(now);

		List<PurchaseOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		purchaseOrderService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ShipMethodDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long purchaseOrderId = 13012L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		expected.setShipMethod(new ShipMethod(NON_EXISTENT_LONG_ID));

		purchaseOrderService.update(expected);
	}

	@Test(expected = PurchaseOrderReadOnlyException.class)
	public void update_PurchaseOrderIsApproved_ThrowPurchaseOrderReadOnlyException() throws Exception {
		Long purchaseOrderId = 13013L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		try {
			purchaseOrderService.update(expected);
		} catch (PurchaseOrderReadOnlyException e) {
			Assert.assertEquals(PurchaseOrderStatus.APPROVED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = PurchaseOrderReadOnlyException.class)
	public void update_PurchaseOrderIsRejected_ThrowPurchaseOrderReadOnlyException() throws Exception {
		Long purchaseOrderId = 13014L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		try {
			purchaseOrderService.update(expected);
		} catch (PurchaseOrderReadOnlyException e) {
			Assert.assertEquals(PurchaseOrderStatus.REJECTED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = PurchaseOrderReadOnlyException.class)
	public void update_PurchaseOrderIsComplete_ThrowPurchaseOrderReadOnlyException() throws Exception {
		Long purchaseOrderId = 13015L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		try {
			purchaseOrderService.update(expected);
		} catch (PurchaseOrderReadOnlyException e) {
			Assert.assertEquals(PurchaseOrderStatus.COMPLETE, e.getStatus());
			throw e;
		}
	}

	@Test(expected = PurchaseOrderReadOnlyException.class)
	public void delete_PurchaseOrderIsApproved_ThrowPurchaseOrderReadOnlyException() throws Exception {
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, 13013L);

		try {
			purchaseOrderService.delete(expected);
		} catch (PurchaseOrderReadOnlyException e) {
			Assert.assertEquals(PurchaseOrderStatus.APPROVED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = PurchaseOrderReadOnlyException.class)
	public void delete_PurchaseOrderIsRejected_ThrowPurchaseOrderReadOnlyException() throws Exception {
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, 13014L);

		try {
			purchaseOrderService.delete(expected);
		} catch (PurchaseOrderReadOnlyException e) {
			Assert.assertEquals(PurchaseOrderStatus.REJECTED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = PurchaseOrderReadOnlyException.class)
	public void delete_PurchaseOrderIsComplete_ThrowPurchaseOrderReadOnlyException() throws Exception {
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, 13015L);

		try {
			purchaseOrderService.delete(expected);
		} catch (PurchaseOrderReadOnlyException e) {
			Assert.assertEquals(PurchaseOrderStatus.COMPLETE, e.getStatus());
			throw e;
		}
	}

	@Test(expected = PurchaseOrderDetailsNullOrEmptyException.class)
	public void create_DetailsIsNull_ThrowPurchaseOrderDetailsNullOrEmptyException() throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("NEW-PO-DETAILS-NULL");
		expected.setVendor(new Vendor(13001L));
		expected.setShipMethod(new ShipMethod(13001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		expected.setDetails(null);
		purchaseOrderService.create(expected);

		em.clear();
	}

	@Test(expected = PurchaseOrderDetailsNullOrEmptyException.class)
	public void create_DetailsIsEmpty_ThrowPurchaseOrderDetailsNullOrEmptyException() throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("NEW-PO-DETAILS-EMPTY");
		expected.setVendor(new Vendor(13001L));
		expected.setShipMethod(new ShipMethod(13001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		expected.setDetails(new ArrayList<PurchaseOrderDetail>());
		purchaseOrderService.create(expected);

		em.clear();
	}

	@Test(expected = PurchaseOrderDetailsNullOrEmptyException.class)
	public void update_DetailsIsNull_ThrowPurchaseOrderDetailsNullOrEmptyException() throws Exception {
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, 13016L);
		expected.setDetails(null);

		purchaseOrderService.update(expected);
	}

	@Test(expected = PurchaseOrderDetailsNullOrEmptyException.class)
	public void update_DetailsIsEmpty_ThrowPurchaseOrderDetailsNullOrEmptyException() throws Exception {
		Long purchaseOrderId = 13016L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		expected.getDetails().clear();

		purchaseOrderService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		PurchaseOrder expected = new PurchaseOrder();
		expected.setReferenceNumber("NEW-PO-PRODUCT-MISSING");
		expected.setVendor(new Vendor(13001L));
		expected.setShipMethod(new ShipMethod(13001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setFreight(150D);
		expected.setTaxAmount(5D);

		PurchaseOrderDetail detail1 = new PurchaseOrderDetail();
		detail1.setOrderedQuantity((short) 2500);
		detail1.setUnitPrice(10D);
		detail1.setProduct(new Product(NON_EXISTENT_LONG_ID));
		detail1.setDueDate(now);

		PurchaseOrderDetail detail2 = new PurchaseOrderDetail();
		detail2.setOrderedQuantity((short) 3000);
		detail2.setUnitPrice(25D);
		detail2.setProduct(new Product(13002L));
		detail2.setDueDate(now);

		List<PurchaseOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		purchaseOrderService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long purchaseOrderId = 13017L;
		PurchaseOrder expected = em.getReference(PurchaseOrder.class, purchaseOrderId);
		List<PurchaseOrderDetail> details = purchaseOrderService.getDetails(purchaseOrderId);
		expected.setDetails(details);

		PurchaseOrderDetail detail = new PurchaseOrderDetail();
		detail.setOrderedQuantity((short) 3000);
		detail.setUnitPrice(25D);
		detail.setProduct(new Product(NON_EXISTENT_LONG_ID));
		detail.setDueDate(new Date());
		expected.getDetails().add(detail);

		purchaseOrderService.update(expected);
	}

	@Test(expected = PurchaseOrderCompleteQuantityOutOfRangeException.class)
	public void deliver_DeliveredQuantityIsGreaterThanShippedQuantity_ThrowPurchaseOrderCompleteQuantityOutOfRangeException()
			throws Exception {
		Long purchaseOrderId = 13018L;
		Long locationId = 13001L;
		Long productId = 13001L;
		Short orderedQuantity = 600;
		Short receivedQuantity = 700;
		Map<Long, Short> productCount = new HashMap<>();
		productCount.put(productId, receivedQuantity);

		try {
			purchaseOrderService.complete(new PurchaseOrder(purchaseOrderId), new Location(locationId), productCount);
		} catch (PurchaseOrderCompleteQuantityOutOfRangeException e) {
			Assert.assertEquals(purchaseOrderId, e.getPurchaseOrderId());
			Assert.assertEquals(productId, e.getProductId());
			Assert.assertEquals(orderedQuantity, e.getOrderedQuantity());
			Assert.assertEquals(receivedQuantity, e.getReceivedQuantity());
			throw e;
		}
	}
}
