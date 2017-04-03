package org.rainbow.security.core.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rainbow.security.core.entities.Membership;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.jdbc.SensitiveUserDetailsEncrypter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/applicationContext.xml")
public class SensitiveUserDetailsEncrypterTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SensitiveUserDetailsEncrypter sensitiveUserDetailsEncrypter;

	private static final MySqlDatabase DATABASE = new MySqlDatabase();

	@BeforeClass
	public static void setUpClass() throws SQLException, IOException {
		DATABASE.execute("src/test/resources/Cleanup.sql");
		DATABASE.execute("src/test/resources/SensitiveUserDetailsEncrypterTestSetup.sql");
	}

	@AfterClass
	public static void cleanUpClass() throws SQLException, IOException {
		DATABASE.execute("src/test/resources/Cleanup.sql");
	}

	@After
	public void tearDown() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void encrypt_UserSensitiveDetailsWereInsertedInTheDbInClearText_UserSensitiveDetailsEncrypted() {
		// Under normal conditions, the SensitiveUserDetailsEncrypter.encrypt
		// method will be called during bean initialization when specified as
		// the init-method of the <bean> element. However, we decided to call it
		// here manually because this test class is not necessarily the first
		// test class to run. This means that it may run after other test
		// classes have run; in other words, it may run after the beans declared
		// in the applicationContext.xml file have all been initialized, and
		// since the SensitiveUserDetailsEncrypter.encrypt method runs only
		// during initialization, the details
		// in the database will not be encrypted and the test will fail. We
		// therefore call the SensitiveUserDetailsEncrypter.encrypt so that this
		// test passes, provided the later method was well implemented.
		sensitiveUserDetailsEncrypter.encrypt();

		User user = em.getReference(User.class, 5001L);
		Membership membership = user.getMembership();
		Assert.assertTrue(membership.isEncrypted());
		Assert.assertTrue(membership.getPassword().startsWith("$2a$16$"));
		Assert.assertTrue(membership.getPasswordQuestionAnswer().startsWith("$2a$16$"));
	}
}