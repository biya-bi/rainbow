package org.rainbow.journal.service;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.rainbow.common.test.DatabaseExternalResource;

@RunWith(Suite.class)
@SuiteClasses({ JournalServiceTest.class, ProfileServiceTest.class, PublicationServiceTest.class,
		SubscriptionServiceTest.class })
public class ServiceTestSuite {
	private static final String CLEAR_DATA = "src/test/resources/SQL/ClearData.sql";

	private static final String RESET_AUTO_INCREMENT = "src/test/resources/SQL/ResetAutoIncrement.sql";

	@ClassRule
	public static final DatabaseExternalResource DATABASE_EXTERNAL_RESOURCE = new DatabaseExternalResource(
			DatabaseProvider.getDatabase(), new String[] { CLEAR_DATA },
			new String[] { CLEAR_DATA, RESET_AUTO_INCREMENT });
}
