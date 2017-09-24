package org.rainbow.asset.explorer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Department;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductIssue;
import org.rainbow.asset.explorer.orm.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.persistence.dao.InventoryManager;
import org.rainbow.asset.explorer.service.services.ProductIssueService;
import org.rainbow.common.test.DatabaseInitialize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/InventoryManagerTestSetup.sql")
public class InventoryManagerTest extends AbstractServiceTest {

	@Autowired
	@Qualifier("inventoryManager")
	private InventoryManager inventoryManager;

	@Autowired
	@Qualifier("productIssueService")
	private ProductIssueService productIssueService;

	@Test
	public void load_LocationIdIsSupplied_InventoryReturned() throws Exception {
		Map<Product, Short> inventory = inventoryManager.load(6001L);
		Assert.assertEquals(3, inventory.size());
	}

	@Test
	public void create_ProductIssueIsValid_EmailAlertSent() throws Exception {
		long locationId = 6002L;

		ProductIssue expected = new ProductIssue();
		expected.setReferenceNumber("NEW-PI-REF");
		expected.setLocation(new Location(locationId));
		expected.setDepartment(new Department(6001));
		expected.setIssueDate(new Date());
		expected.setRequisitioner("Requisitioner 1");

		ProductIssueDetail detail1 = new ProductIssueDetail();
		detail1.setQuantity((short) 1500);
		detail1.setProduct(new Product(6001L));

		ProductIssueDetail detail2 = new ProductIssueDetail();
		detail2.setQuantity((short) 200);
		detail2.setProduct(new Product(6002L));

		ProductIssueDetail detail3 = new ProductIssueDetail();
		detail3.setQuantity((short) 400);
		detail3.setProduct(new Product(6003L));

		ProductIssueDetail detail4 = new ProductIssueDetail();
		detail4.setQuantity((short) 1000);
		detail4.setProduct(new Product(6004L));

		List<ProductIssueDetail> details = new ArrayList<>();
		details.add(detail1);
		details.add(detail2);
		details.add(detail3);
		details.add(detail4);

		expected.setDetails(details);

		productIssueService.create(expected);

	}
}
