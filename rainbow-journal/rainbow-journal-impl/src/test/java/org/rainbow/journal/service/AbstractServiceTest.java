package org.rainbow.journal.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.rainbow.common.test.Database;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.common.test.TestClassWatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/Spring/applicationContext.xml")
public abstract class AbstractServiceTest {

	private static final String CLEAR_DATA = "src/test/resources/SQL/ClearData.sql";

	@ClassRule
	public static final TestClassWatcher TEST_CLASS_WATCHER = new TestClassWatcher();

	@BeforeClass
	public static void setUp() throws FileNotFoundException, SQLException, IOException {
		final Database database = DatabaseProvider.getDatabase();
		database.execute(CLEAR_DATA);
		initilize(database);
	}

	@AfterClass
	public static void cleanUp() throws FileNotFoundException, SQLException, IOException {
		final Database database = DatabaseProvider.getDatabase();
		database.execute(CLEAR_DATA);
	}

	private static void initilize(Database db) throws FileNotFoundException, SQLException, IOException {
		final Class<?> testClass = TEST_CLASS_WATCHER.getTestClass();
		if (testClass != null) {
			String[] initScripts = getInitScripts(testClass);
			if (initScripts != null) {
				for (String script : initScripts) {
					if (script != null && !script.trim().isEmpty()) {
						db.execute(script.trim());
					}
				}
			}
		}
	}

	private static String[] getInitScripts(Class<?> cls) {
		DatabaseInitialize annotation = cls.getAnnotation(DatabaseInitialize.class);
		if (annotation != null) {
			return annotation.value();
		}
		return null;
	}
}
