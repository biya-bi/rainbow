package org.rainbow.asset.explorer.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.rainbow.common.test.Database;
import org.rainbow.common.test.DatabaseExternalResource;
import org.rainbow.common.test.DatabaseImpl;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.common.test.TestClassWatcher;

@RunWith(Suite.class)
@SuiteClasses({ AlertServiceTest.class, AssetServiceTest.class, AssetTypeServiceTest.class,
		EmailRecipientServiceTest.class, EmailTemplateServiceTest.class, InventoryManagerTest.class,
		LocaleServiceTest.class, LocationServiceTest.class, ManufacturerServiceTest.class,
		ProductIssueServiceTest.class, ProductReceiptServiceTest.class, ProductServiceTest.class,
		PurchaseOrderServiceTest.class, ShipMethodServiceTest.class, ShippingOrderServiceTest.class,
		SiteServiceTest.class, VendorServiceTest.class })
public class ServiceTestSuite {
	
	private static Database database = new DatabaseImpl("jdbc:mysql://localhost:3306/rainbow_asset_explorer_dev",
			"root", "Passw0rd", "com.mysql.jdbc.Driver");

	@ClassRule
	public static final TestClassWatcher TEST_CLASS_WATCHER = new TestClassWatcher();

	@ClassRule
	public static final DatabaseExternalResource DATABASE_EXTERNAL_RESOURCE = new DatabaseExternalResource(database,
			null, new String[] { "src/test/resources/ClearData.sql", "src/test/resources/ResetAutoIncrement.sql" });

	private static String[] getInitScripts(Class<?> cls) {
		DatabaseInitialize annotation = cls.getAnnotation(DatabaseInitialize.class);
		if (annotation != null) {
			return annotation.value();
		}
		return null;
	}

	@BeforeClass
	public static void setUp() throws FileNotFoundException, SQLException, IOException {
		database.execute("src/test/resources/ClearData.sql");

		String[] initScripts = getInitScripts(TEST_CLASS_WATCHER.getTestClass());
		if (initScripts != null) {
			for (String script : initScripts) {
				if (script != null && !script.trim().isEmpty()) {
					database.execute(script.trim());
				}
			}
		}
	}

	@AfterClass
	public static void cleanUp() throws FileNotFoundException, SQLException, IOException {
		database.execute("src/test/resources/ClearData.sql");
	}
}
