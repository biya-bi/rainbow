package org.rainbow.asset.explorer.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.Manufacturer;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductNameException;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductNumberException;
import org.rainbow.asset.explorer.service.services.ProductService;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/ProductServiceTestSetup.sql")
public class ProductServiceTest extends AbstractServiceTest {

	private static final long NON_EXISTENT_LONG_ID = 5000000L;

	@Autowired
	@Qualifier("productService")
	private ProductService productService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	/**
	 * Test of create method, of class ProductDao.
	 *
	 * @throws java.lang.Exception
	 */
	@Test
	public void create_ProductIsValid_ProductCreated() throws Exception {
		Date now = new Date();
		Product expected = new Product("PDT-1500", "New Product 1", (short) 1000, (short) 750, (short) 3, "", now, now);

		productService.create(expected);

		Product actual = em.getReference(Product.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	/**
	 * Test of create method, of class ProductDao.
	 *
	 * @throws java.lang.Exception
	 */
	@Test(expected = DuplicateProductNameException.class)
	public void create_ProductNameAlreadyExists_ThrowDuplicateProductNameException() throws Exception {
		Date now = new Date();
		Product product = new Product("PDT-1501", "Sample Product 1", (short) 1000, (short) 750, (short) 3, "", now,
				now);

		try {
			productService.create(product);
		} catch (DuplicateProductNameException e) {
			Assert.assertEquals(product.getName(), e.getName());
			throw e;
		}
	}

	@Test
	public void delete_ProductExists_ProductDeleted() throws Exception {
		// The below product represents Sample Product 2
		final Product product = new Product(12002L);
		boolean deleted = false;

		productService.delete(product);

		try {
			em.getReference(Product.class, product.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_ProductDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final Product product = new Product(NON_EXISTENT_LONG_ID);
		productService.delete(product);
	}

	@Test
	public void update_ProductIsValid_ProductEdited() throws Exception {
		// Get Sample Product 3
		Product expected = em.getReference(Product.class, 12003L);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		productService.update(expected);

		Product actual = em.getReference(Product.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateProductNameException.class)
	public void update_ProductNameAlreadyExists_ThrowDuplicateProductNameException() throws Exception {
		// Get Sample Product 5
		Product product = em.getReference(Product.class, 12005L);
		product.setName("Sample Product 4");

		try {
			productService.update(product);
		} catch (DuplicateProductNameException e) {
			Assert.assertEquals(product.getName(), e.getName());
			throw e;
		}
	}

	@Test(expected = DuplicateProductNumberException.class)
	public void create_ProductNumberAlreadyExists_ThrowDuplicateProductNumberException() throws Exception {
		Date now = new Date();
		Product product = new Product("PDT-12006", "Product with duplicate number", (short) 1000, (short) 750, (short) 3,
				"", now, now);

		try {
			productService.create(product);
		} catch (DuplicateProductNumberException e) {
			Assert.assertEquals(product.getNumber(), e.getNumber());
			throw e;
		}
	}

	@Test(expected = DuplicateProductNumberException.class)
	public void update_ProductNumberAlreadyExists_ThrowDuplicateProductNumberException() throws Exception {
		// Get Sample Product 8
		Product product = em.getReference(Product.class, 12008L);
		product.setNumber("PDT-12007");

		try {
			productService.update(product);
		} catch (DuplicateProductNumberException e) {
			Assert.assertEquals(product.getNumber(), e.getNumber());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_ManufacturerDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Date now = new Date();

		Product expected = new Product("PDT-MISS-MAN-001", "NEW-PRO-MANU-MISSING", (short) 1000, (short) 750, (short) 3,
				"", now, now);
		expected.setManufacturer(new Manufacturer(NON_EXISTENT_LONG_ID));

		productService.create(expected);
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_ManufacturerDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		Product expected = em.getReference(Product.class, 12003L);
		expected.setManufacturer(new Manufacturer(NON_EXISTENT_LONG_ID));

		productService.update(expected);
	}

	@Test
	public void find_ProductNumbersProvided_ReturnProductsWithProvidedNumbers() {
		List<String> productNumbers = new ArrayList<>();
		productNumbers.add("PDT-12009");
		productNumbers.add("PDT-12010");
		productNumbers.add("PDT-12011");

		List<Product> result = productService.find(productNumbers);

		Assert.assertEquals(3, result.size());
	}
}
