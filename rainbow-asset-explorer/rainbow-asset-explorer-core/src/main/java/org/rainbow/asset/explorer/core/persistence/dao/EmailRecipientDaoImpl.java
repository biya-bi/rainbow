/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.EmailRecipient;
import org.rainbow.asset.explorer.core.entities.Locale;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateEmailRecipientEmailException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class EmailRecipientDaoImpl extends TrackableDaoImpl<EmailRecipient, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public EmailRecipientDaoImpl() {
		super(EmailRecipient.class);
	}

	@Override
	public void create(EmailRecipient emailRecipient) throws Exception {
		Locale locale = emailRecipient.getLocale();
		if (locale != null) {
			emailRecipient.setLocale(new EntityManagerHelper(em).find(Locale.class, locale));
		}
		super.create(emailRecipient);
	}

	@Override
	public void update(EmailRecipient emailRecipient) throws Exception {
		Locale locale = emailRecipient.getLocale();
		if (locale != null) {
			emailRecipient.setLocale(new EntityManagerHelper(em).find(Locale.class, locale));
		}
		super.update(emailRecipient);
	}

	@Override
	protected void validate(EmailRecipient emailRecipient, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Integer id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = emailRecipient.getId();
			if (helper.isDuplicate(EmailRecipient.class, emailRecipient.getEmail(), "email", "id", id)) {
				throw new DuplicateEmailRecipientEmailException(emailRecipient.getEmail());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
