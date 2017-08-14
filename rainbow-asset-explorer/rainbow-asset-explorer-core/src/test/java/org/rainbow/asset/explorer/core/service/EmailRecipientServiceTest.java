/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.junit.Assert;
import org.junit.Test;
import org.rainbow.asset.explorer.core.entities.EmailRecipient;
import org.rainbow.asset.explorer.core.entities.Locale;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateEmailRecipientEmailException;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.common.test.DatabaseInitialize;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.exceptions.NonexistentEntityException;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@DatabaseInitialize("src/test/resources/EmailRecipientServiceTestSetup.sql")
public class EmailRecipientServiceTest extends AbstractServiceTest {

	private static final int NON_EXISTENT_INT_ID = 5000;

	@Autowired
	@Qualifier("emailRecipientService")
	private Service<EmailRecipient, Integer, SearchOptions> emailRecipientService;

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Test
	public void create_EmailRecipientIsValid_EmailRecipientCreated() throws Exception {
		EmailRecipient expected = new EmailRecipient("New Recipient", "new.email.recipient@optimum.org");
		expected.setLocale(new Locale(4001));

		emailRecipientService.create(expected);

		EmailRecipient actual = em.getReference(EmailRecipient.class, expected.getId());
		actual.getId(); // If no exception is thrown, then the create was
						// successful.
	}

	@Test(expected = DuplicateEmailRecipientEmailException.class)
	public void create_EmailAlreadyExists_ThrowDuplicateEmailRecipientEmailException() throws Exception {
		EmailRecipient emailRecipient = new EmailRecipient("Duplicate Email Recipient", "email4001@optimum.org");
		emailRecipient.setLocale(new Locale(4001));

		try {
			emailRecipientService.create(emailRecipient);
		} catch (DuplicateEmailRecipientEmailException e) {
			Assert.assertEquals(emailRecipient.getEmail(), e.getEmail());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void create_LocaleDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		EmailRecipient expected = new EmailRecipient("Non-existent Locale Recipient",
				"email.recipient.none.existent.locale@optimum.org");
		expected.setLocale(new Locale(NON_EXISTENT_INT_ID));

		emailRecipientService.create(expected);
	}

	@Test
	public void delete_EmailRecipientExists_EmailRecipientDeleted() throws Exception {
		final EmailRecipient emailRecipient = new EmailRecipient(4002);
		boolean deleted = false;

		emailRecipientService.delete(emailRecipient);

		try {
			em.getReference(EmailRecipient.class, emailRecipient.getId());
		} catch (EntityNotFoundException e) {
			deleted = true;
		}
		Assert.assertTrue(deleted);
	}

	@Test(expected = NonexistentEntityException.class)
	public void delete_EmailRecipientDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final EmailRecipient emailRecipient = new EmailRecipient(NON_EXISTENT_INT_ID);
		emailRecipientService.delete(emailRecipient);
	}

	@Test
	public void update_EmailRecipientIsValid_EmailRecipientEdited() throws Exception {
		EmailRecipient expected = em.getReference(EmailRecipient.class, 4003);
		expected.setEmail("email_102@optimum.org");

		em.clear();

		emailRecipientService.update(expected);

		EmailRecipient actual = em.getReference(EmailRecipient.class, expected.getId());
		Assert.assertEquals(expected.getEmail(), actual.getEmail());
	}

	@Test(expected = DuplicateEmailRecipientEmailException.class)
	public void update_EmailAlreadyExists_ThrowDuplicateEmailRecipientEmailException() throws Exception {
		EmailRecipient emailRecipient = em.getReference(EmailRecipient.class, 4005);
		emailRecipient.setEmail("email4004@optimum.org");

		try {
			emailRecipientService.update(emailRecipient);
		} catch (DuplicateEmailRecipientEmailException e) {
			Assert.assertEquals(emailRecipient.getEmail(), e.getEmail());
			throw e;
		}
	}

	@Test(expected = NonexistentEntityException.class)
	public void update_LocaleDoesNotExist_ThrowNonexistentEntityException() throws Exception {
		final EmailRecipient emailRecipient = em.getReference(EmailRecipient.class, 4006);
		emailRecipient.setLocale(new Locale(NON_EXISTENT_INT_ID));
		emailRecipientService.update(emailRecipient);
	}
}
