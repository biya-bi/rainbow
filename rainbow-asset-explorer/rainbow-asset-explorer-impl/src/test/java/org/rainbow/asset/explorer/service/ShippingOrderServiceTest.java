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
import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderStatus;
import org.rainbow.asset.explorer.persistence.dao.InventoryManager;
import org.rainbow.asset.explorer.service.exceptions.DuplicateShippingOrderReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderDeliveredQuantityOutOfRangeException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderLocationException;
import org.rainbow.asset.explorer.service.exceptions.ShippingOrderReadOnlyException;
import org.rainbow.asset.explorer.service.services.ShippingOrderService;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/ShippingOrderServiceTestSetup.sql")
public class ShippingOrderServiceTest extends AbstractServiceTest {

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("shippingOrderService")
	private ShippingOrderService shippingOrderService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("inventoryManager")
	private InventoryManager inventoryManager;

	@Test
	public void create_ShippingOrderIsValid_ShippingOrderCreated() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("NEW-SPO-REF");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15002L));

		ShippingOrderDetail detail1 = new ShippingOrderDetail();
		detail1.setShippedQuantity((short) 2500);
		detail1.setProduct(new Product(15001L));

		ShippingOrderDetail detail2 = new ShippingOrderDetail();
		detail2.setShippedQuantity((short) 3000);
		detail2.setProduct(new Product(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		shippingOrderService.create(expected);

		em.clear();

		ShippingOrder actual = em.getReference(ShippingOrder.class, expected.getId());
		actual.setDetails(shippingOrderService.getDetails(actual.getId()));
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
		Assert.assertEquals(ShippingOrderStatus.PENDING, actual.getStatus());
		Assert.assertEquals(expected.getDetails().size(), actual.getDetails().size());
	}

	@Test(expected = DuplicateShippingOrderReferenceNumberException.class)
	public void create_ReferenceNumberAlreadyExists_ThrowDuplicateShippingOrderReferenceNumberException()
			throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("SPO-15001");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15002L));

		ShippingOrderDetail detail1 = new ShippingOrderDetail();
		detail1.setShippedQuantity((short) 2500);
		detail1.setProduct(new Product(15001L));

		ShippingOrderDetail detail2 = new ShippingOrderDetail();
		detail2.setShippedQuantity((short) 3000);
		detail2.setProduct(new Product(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		try {
			shippingOrderService.create(expected);
		} catch (DuplicateShippingOrderReferenceNumberException e) {
			Assert.assertEquals(expected.getReferenceNumber(), e.getReferenceNumber());
			throw e;
		}
	}

	@Test(expected = DuplicateShippingOrderReferenceNumberException.class)
	public void update_ReferenceNumberAlreadyExists_ThrowDuplicateShippingOrderReferenceNumberException()
			throws Exception {
		Long shippingOrderId = 15003L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		expected.setReferenceNumber("SPO-15002");

		try {
			shippingOrderService.update(expected);
		} catch (DuplicateShippingOrderReferenceNumberException e) {
			Assert.assertEquals(expected.getReferenceNumber(), e.getReferenceNumber());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_SourceLocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("SPO-SOURCE-LOC-MIS");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(NON_EXISTENT_LONG_ID));
		expected.setTargetLocation(new Location(15002L));

		ShippingOrderDetail detail1 = new ShippingOrderDetail();
		detail1.setShippedQuantity((short) 2500);
		detail1.setProduct(new Product(15001L));

		ShippingOrderDetail detail2 = new ShippingOrderDetail();
		detail2.setShippedQuantity((short) 3000);
		detail2.setProduct(new Product(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		shippingOrderService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_TargetLocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("SPO-TARGET-LOC-MIS");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(NON_EXISTENT_LONG_ID));

		ShippingOrderDetail detail1 = new ShippingOrderDetail();
		detail1.setShippedQuantity((short) 2500);
		detail1.setProduct(new Product(15001L));

		ShippingOrderDetail detail2 = new ShippingOrderDetail();
		detail2.setShippedQuantity((short) 3000);
		detail2.setProduct(new Product(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		shippingOrderService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_SourceLocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long shippingOrderId = 15004L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		expected.setSourceLocation(new Location(NON_EXISTENT_LONG_ID));

		shippingOrderService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_TargetLocationDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long shippingOrderId = 15004L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		expected.setTargetLocation(new Location(NON_EXISTENT_LONG_ID));

		shippingOrderService.update(expected);
	}

	@Test(expected = ShippingOrderLocationException.class)
	public void create_SourceAndTargetLocationsAreEqual_ThrowShippingOrderLocationException() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("NEW-SPO-SAM-LOC");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);

		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15001L));

		ShippingOrderDetail detail1 = new ShippingOrderDetail();
		detail1.setShippedQuantity((short) 2500);
		detail1.setProduct(new Product(15001L));

		ShippingOrderDetail detail2 = new ShippingOrderDetail();
		detail2.setShippedQuantity((short) 3000);
		detail2.setProduct(new Product(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		try {
			shippingOrderService.create(expected);
		} catch (ShippingOrderLocationException e) {
			Assert.assertEquals(expected.getSourceLocation().getId(), e.getLocationId());
			Assert.assertEquals(expected.getTargetLocation().getId(), e.getLocationId());
			throw e;
		}
	}

	@Test(expected = ShippingOrderLocationException.class)
	public void update_SourceAndTargetLocationsAreEqual_ThrowShippingOrderLocationException() throws Exception {
		Long shippingOrderId = 15004L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15001L));

		try {
			shippingOrderService.update(expected);
		} catch (ShippingOrderLocationException e) {
			Assert.assertEquals(expected.getSourceLocation().getId(), e.getLocationId());
			Assert.assertEquals(expected.getTargetLocation().getId(), e.getLocationId());
			throw e;
		}
	}

	@Test
	public void approve_ShippingOrderIsPending_ShippingOrderApproved() throws Exception {
		Long id = 15005L;

		shippingOrderService.approve(new ShippingOrder(id));

		em.clear();
		ShippingOrder actual = em.getReference(ShippingOrder.class, id);
		Assert.assertEquals(ShippingOrderStatus.APPROVED, actual.getStatus());
	}

	@Test(expected = NonexistentEntityException.class)
	public void approve_ShippingOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		shippingOrderService.approve(new ShippingOrder(NON_EXISTENT_LONG_ID));
	}

	@Test
	public void reject_ShippingOrderIsPending_ShippingOrderRejected() throws Exception {
		Long id = 15006L;

		shippingOrderService.reject(new ShippingOrder(id));

		em.clear();
		ShippingOrder actual = em.getReference(ShippingOrder.class, id);
		Assert.assertEquals(ShippingOrderStatus.REJECTED, actual.getStatus());
	}

	@Test(expected = NonexistentEntityException.class)
	public void reject_ShippingOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		shippingOrderService.reject(new ShippingOrder(NON_EXISTENT_LONG_ID));
	}

	@Test
	public void transit_ShippingOrderIsApproved_ShippingOrderInTransit() throws Exception {
		Long shippingOrderId = 15007L;
		Long locationId = 15003L;

		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(locationId);

		ShippingOrder shippingOrder = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		shippingOrder.setDetails(details);

		em.clear();

		shippingOrderService.transit(new ShippingOrder(shippingOrderId));

		ShippingOrder actual = em.getReference(ShippingOrder.class, shippingOrderId);
		Assert.assertEquals(ShippingOrderStatus.IN_TRANSIT, actual.getStatus());

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(locationId);

		int matches = 0;
		for (ProductInventory pib : inventoryBefore) {
			for (ProductInventory pia : inventoryAfter) {
				if (pib.getId().getProductId().equals(pia.getId().getProductId())) {
					Short shippedQuantity = 0;
					for (ShippingOrderDetail detail : details) {
						if (detail.getProduct().getId().equals(pia.getId().getProductId())) {
							shippedQuantity = (short) (shippedQuantity + detail.getShippedQuantity());
						}
					}
					if (pib.getQuantity() - shippedQuantity == pia.getQuantity()) {
						matches++;
					}
					break;
				}
			}
		}

		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryBefore.size());
	}

	@Test(expected = NonexistentEntityException.class)
	public void transit_ShippingOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		shippingOrderService.transit(new ShippingOrder(NON_EXISTENT_LONG_ID));
	}

	@Test
	public void restitute_ShippingOrderIsApproved_ShippingOrderRestituted() throws Exception {
		Long shippingOrderId = 15008L;
		Long locationId = 15005L;

		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(locationId);
		ShippingOrder shippingOrder = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		shippingOrder.setDetails(details);

		em.clear();

		shippingOrderService.restitute(new ShippingOrder(shippingOrderId));

		ShippingOrder actual = em.getReference(ShippingOrder.class, shippingOrderId);
		Assert.assertEquals(ShippingOrderStatus.RESTITUTED, actual.getStatus());

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(locationId);

		int matches = 0;
		for (ProductInventory pib : inventoryBefore) {
			for (ProductInventory pia : inventoryAfter) {
				if (pib.getId().getProductId().equals(pia.getId().getProductId())) {
					Short shippedQuantity = 0;
					for (ShippingOrderDetail detail : details) {
						if (detail.getProduct().getId().equals(pia.getId().getProductId())) {
							shippedQuantity = (short) (shippedQuantity + detail.getShippedQuantity());
						}
					}
					if (pib.getQuantity() + shippedQuantity == pia.getQuantity()) {
						matches++;
					}
					break;
				}
			}
		}

		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryBefore.size());
	}

	@Test(expected = NonexistentEntityException.class)
	public void restitute_ShippingOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		shippingOrderService.restitute(new ShippingOrder(NON_EXISTENT_LONG_ID));
	}

	@Test
	public void deliver_ShippingOrderIsInTransit_ShippingOrderDelivered() throws Exception {
		Long shippingOrderId = 15009L;
		Long targetLocationId = 15008L;

		List<ProductInventory> inventoryBefore = inventoryManager.getProductInventories(targetLocationId);

		em.clear();

		Map<Long, Short> productCount = new HashMap<>();
		productCount.put(15001L, (short) 1400);

		shippingOrderService.deliver(new ShippingOrder(shippingOrderId), productCount);

		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);

		ShippingOrder actual = em.getReference(ShippingOrder.class, shippingOrderId);
		Assert.assertEquals(ShippingOrderStatus.DELIVERED, actual.getStatus());

		List<ProductInventory> inventoryAfter = inventoryManager.getProductInventories(targetLocationId);

		int matches = 0;

		Map<Long, Short> ivb = new HashMap<>();
		for (ShippingOrderDetail detail : details) {
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
							.getShippedQuantity() == detail.getReceivedQuantity() + detail.getRejectedQuantity()) {
						matches++;
					}
				}
			}
		}
		Assert.assertTrue(matches > 0);
		Assert.assertEquals(matches, inventoryAfter.size());
	}

	@Test
	public void delete_ShippingOrderExists_ShippingOrderDeleted() throws Exception {
		final ShippingOrder shippingOrder = new ShippingOrder(15010L);
		boolean deleted = false;

		shippingOrderService.delete(shippingOrder);

		try {
			em.getReference(ShippingOrder.class, shippingOrder.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ShippingOrderDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final ShippingOrder shippingOrder = new ShippingOrder(NON_EXISTENT_LONG_ID);
		shippingOrderService.delete(shippingOrder);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ShipMethodDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("SPO-SHIP_METHOD-MIS");
		expected.setShipMethod(new ShipMethod(NON_EXISTENT_LONG_ID));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15013L));

		ShippingOrderDetail detail1 = new ShippingOrderDetail();
		detail1.setShippedQuantity((short) 2500);
		detail1.setProduct(new Product(15001L));

		ShippingOrderDetail detail2 = new ShippingOrderDetail();
		detail2.setShippedQuantity((short) 3000);
		detail2.setProduct(new Product(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		shippingOrderService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ShipMethodDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long shippingOrderId = 15011L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		expected.setShipMethod(new ShipMethod(NON_EXISTENT_LONG_ID));

		shippingOrderService.update(expected);
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void update_ShippingOrderIsApproved_ThrowShippingOrderReadOnlyException() throws Exception {
		Long shippingOrderId = 15012L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		try {
			shippingOrderService.update(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.APPROVED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void update_ShippingOrderIsRejected_ThrowShippingOrderReadOnlyException() throws Exception {
		Long shippingOrderId = 15013L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		try {
			shippingOrderService.update(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.REJECTED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void update_ShippingOrderIsInTransit_ThrowShippingOrderReadOnlyException() throws Exception {
		Long shippingOrderId = 15014L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		try {
			shippingOrderService.update(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.IN_TRANSIT, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void update_ShippingOrderIsDelivered_ThrowShippingOrderReadOnlyException() throws Exception {
		Long shippingOrderId = 15015L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		try {
			shippingOrderService.update(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.DELIVERED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void delete_ShippingOrderIsApproved_ThrowShippingOrderReadOnlyException() throws Exception {
		ShippingOrder expected = em.getReference(ShippingOrder.class, 15012L);

		try {
			shippingOrderService.delete(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.APPROVED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void delete_ShippingOrderIsRejected_ThrowShippingOrderReadOnlyException() throws Exception {
		ShippingOrder expected = em.getReference(ShippingOrder.class, 15013L);

		try {
			shippingOrderService.delete(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.REJECTED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void delete_ShippingOrderIsInTransit_ThrowShippingOrderReadOnlyException() throws Exception {
		ShippingOrder expected = em.getReference(ShippingOrder.class, 15014L);

		try {
			shippingOrderService.delete(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.IN_TRANSIT, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderReadOnlyException.class)
	public void delete_ShippingOrderIsDelivered_ThrowShippingOrderReadOnlyException() throws Exception {
		ShippingOrder expected = em.getReference(ShippingOrder.class, 15015L);

		try {
			shippingOrderService.delete(expected);
		} catch (ShippingOrderReadOnlyException e) {
			Assert.assertEquals(ShippingOrderStatus.DELIVERED, e.getStatus());
			throw e;
		}
	}

	@Test(expected = ShippingOrderDetailsNullOrEmptyException.class)
	public void create_DetailsIsNull_ThrowShippingOrderDetailsNullOrEmptyException() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("NEW-SPO-DETAILS-NULL");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15002L));

		expected.setDetails(null);

		shippingOrderService.create(expected);
	}

	@Test(expected = ShippingOrderDetailsNullOrEmptyException.class)
	public void create_DetailsIsEmpty_ThrowShippingOrderDetailsNullOrEmptyException() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("NEW-SPO-DETAILS-EMPTY");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15002L));

		expected.setDetails(new ArrayList<ShippingOrderDetail>());

		shippingOrderService.create(expected);
	}

	@Test(expected = ShippingOrderDetailsNullOrEmptyException.class)
	public void create_TheOnlyDetailHasZeroShippedQuantity_ThrowShippingOrderDetailsNullOrEmptyException()
			throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("NEW-SPO-ZERO-QTY-DETAIL");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();

		ShippingOrderDetail detail = new ShippingOrderDetail();
		detail.setShippedQuantity((short) 0);
		detail.setProduct(new Product(15001L));

		details.add(detail);
		expected.setDetails(details);

		shippingOrderService.create(expected);
	}

	@Test(expected = ShippingOrderDetailsNullOrEmptyException.class)
	public void update_DetailsIsNull_ThrowShippingOrderDetailsNullOrEmptyException() throws Exception {
		ShippingOrder expected = em.getReference(ShippingOrder.class, 15016L);
		expected.setDetails(null);

		shippingOrderService.update(expected);
	}

	@Test(expected = ShippingOrderDetailsNullOrEmptyException.class)
	public void update_DetailsIsEmpty_ThrowShippingOrderDetailsNullOrEmptyException() throws Exception {
		Long shippingOrderId = 15016L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);
		expected.getDetails().clear();

		shippingOrderService.update(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		ShippingOrder expected = new ShippingOrder();
		expected.setReferenceNumber("NEW-SPO-PRODUCT-MISSING");
		expected.setShipMethod(new ShipMethod(15001L));
		expected.setShipDate(now);
		expected.setCreationDate(now);
		expected.setLastUpdateDate(now);
		expected.setSourceLocation(new Location(15001L));
		expected.setTargetLocation(new Location(15002L));

		ShippingOrderDetail detail1 = new ShippingOrderDetail();
		detail1.setShippedQuantity((short) 2500);
		detail1.setProduct(new Product(NON_EXISTENT_LONG_ID));

		ShippingOrderDetail detail2 = new ShippingOrderDetail();
		detail2.setShippedQuantity((short) 3000);
		detail2.setProduct(new Product(15002L));

		List<ShippingOrderDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);

		expected.setDetails(details);

		shippingOrderService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Long shippingOrderId = 15017L;
		ShippingOrder expected = em.getReference(ShippingOrder.class, shippingOrderId);
		List<ShippingOrderDetail> details = shippingOrderService.getDetails(shippingOrderId);
		expected.setDetails(details);

		ShippingOrderDetail detail = new ShippingOrderDetail();
		detail.setShippedQuantity((short) 3000);
		detail.setProduct(new Product(NON_EXISTENT_LONG_ID));

		expected.getDetails().add(detail);

		shippingOrderService.update(expected);
	}

	@Test(expected = ShippingOrderDeliveredQuantityOutOfRangeException.class)
	public void deliver_DeliveredQuantityIsGreaterThanShippedQuantity_ThrowShippingOrderDeliveredQuantityOutOfRangeException()
			throws Exception {
		Long shippingOrderId = 15018L;
		Long productId = 15001L;
		Short shippedQuantity = 950;
		Short deliveredQuantity = 960;
		Map<Long, Short> productCount = new HashMap<>();
		productCount.put(productId, deliveredQuantity);

		try {
			shippingOrderService.deliver(new ShippingOrder(shippingOrderId), productCount);
		} catch (ShippingOrderDeliveredQuantityOutOfRangeException e) {
			Assert.assertEquals(shippingOrderId, e.getShippingOrderId());
			Assert.assertEquals(productId, e.getProductId());
			Assert.assertEquals(shippedQuantity, e.getShippedQuantity());
			Assert.assertEquals(deliveredQuantity, e.getDeliveredQuantity());
			throw e;
		}
	}
}
