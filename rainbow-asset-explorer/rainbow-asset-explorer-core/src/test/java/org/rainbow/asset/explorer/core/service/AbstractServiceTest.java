package org.rainbow.asset.explorer.core.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.rainbow.common.test.Database;
import org.rainbow.common.test.DatabaseImpl;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.common.test.TestClassWatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public abstract class AbstractServiceTest {

	private static Database database = new DatabaseImpl("jdbc:mysql://localhost:3306/rainbow_asset_explorer_dev",
			"root", "Passw0rd", "com.mysql.jdbc.Driver");

	@ClassRule
	public static final TestClassWatcher TEST_CLASS_WATCHER = new TestClassWatcher();

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
}
