package org.rainbow.asset.explorer.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.service.exceptions.DuplicateEmailTemplateNameException;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/SQL/EmailTemplateServiceTestSetup.sql")
public class EmailTemplateServiceTest extends AbstractServiceTest {

	private static final int NON_EXISTENT_INT_ID = 5000000;

	@Autowired
	@Qualifier("emailTemplateService")
	private Service<EmailTemplate, Integer, SearchOptions> emailTemplateService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_EmailTemplateIsValid_EmailTemplateCreated() throws Exception {
		EmailTemplate expected = new EmailTemplate("NEW-EMAIL-TEMPLATE", "SUBJECT", "CONTENT", new Locale(5001));

		emailTemplateService.create(expected);

		EmailTemplate actual = em.getReference(EmailTemplate.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateEmailTemplateNameException.class)
	public void create_NameAlreadyExists_ThrowDuplicateEmailTemplateNameException() throws Exception {
		EmailTemplate emailTemplate = new EmailTemplate("Email Template 5001", "SUBJECT", "CONTENT", new Locale(5001));

		try {
			emailTemplateService.create(emailTemplate);
		} catch (DuplicateEmailTemplateNameException e) {
			Assert.assertEquals(emailTemplate.getName(), e.getName());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_LocaleDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		EmailTemplate expected = new EmailTemplate("NEMAIL-TEMPLATE-WITH-NON-EXISTENT-LOCALE", "SUBJECT", "CONTENT",
				new Locale(NON_EXISTENT_INT_ID));

		emailTemplateService.create(expected);
	}

	@Test
	public void delete_EmailTemplateExists_EmailTemplateDeleted() throws Exception {
		final EmailTemplate emailTemplate = new EmailTemplate(5002);
		boolean deleted = false;

		emailTemplateService.delete(emailTemplate);

		try {
			em.getReference(EmailTemplate.class, emailTemplate.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_EmailTemplateDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final EmailTemplate emailTemplate = new EmailTemplate(NON_EXISTENT_INT_ID);
		emailTemplateService.delete(emailTemplate);
	}

	@Test
	public void update_EmailTemplateIsValid_EmailTemplateEdited() throws Exception {
		EmailTemplate expected = em.getReference(EmailTemplate.class, 5003);
		expected.setName(expected.getName() + " Renamed");

		em.clear();

		emailTemplateService.update(expected);

		EmailTemplate actual = em.getReference(EmailTemplate.class, expected.getId());
		Assert.assertEquals(expected.getName(), actual.getName());
	}

	@Test(expected = DuplicateEmailTemplateNameException.class)
	public void update_NameAlreadyExists_ThrowDuplicateEmailTemplateNameException() throws Exception {
		EmailTemplate emailTemplate = em.getReference(EmailTemplate.class, 5005);
		emailTemplate.setName("Email Template 5004");

		try {
			emailTemplateService.update(emailTemplate);
		} catch (DuplicateEmailTemplateNameException e) {
			Assert.assertEquals(emailTemplate.getName(), e.getName());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_LocaleDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final EmailTemplate emailTemplate = em.getReference(EmailTemplate.class, 5006);
		emailTemplate.setLocale(new Locale(NON_EXISTENT_INT_ID));
		emailTemplateService.update(emailTemplate);
	}

}
