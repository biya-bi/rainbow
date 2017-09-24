package org.rainbow.security.service.services;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.encryption.SensitiveUserDetailsEncrypter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author Biya-Bi
 *
 */
@DatabaseInitialize("src/test/resources/SQL/SensitiveUserDetailsEncrypterTestSetup.sql")
public class SensitiveUserDetailsEncrypterTest extends AbstractServiceTest {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private SensitiveUserDetailsEncrypter sensitiveUserDetailsEncrypter;

	private static final String SECURITY_DETAIL_PREFIX = "$2a$04$";

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
		// therefore call the SensitiveUserDetailsEncrypterImpl.encrypt so that
		// this
		// test passes, provided the later method was well implemented.
		sensitiveUserDetailsEncrypter.encrypt();

		User user = em.getReference(User.class, 5001L);
		Membership membership = user.getMembership();
		Assert.assertTrue(membership.isEncrypted());
		Assert.assertTrue(membership.getPassword().startsWith(SECURITY_DETAIL_PREFIX));
		Assert.assertEquals(3, membership.getRecoveryInformation().stream()
				.filter(x -> x.getAnswer().startsWith(SECURITY_DETAIL_PREFIX)).map(x -> x.getAnswer()).count());
	}
}