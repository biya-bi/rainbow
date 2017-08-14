/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.EmailTemplate;
import org.rainbow.asset.explorer.core.entities.Locale;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateEmailTemplateNameException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class EmailTemplateDaoImpl extends TrackableDaoImpl<EmailTemplate, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public EmailTemplateDaoImpl() {
		super(EmailTemplate.class);
	}

	@Override
	protected void validate(EmailTemplate emailTemplate, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Integer id;

		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = emailTemplate.getId();
			if (helper.isDuplicate(EmailTemplate.class, emailTemplate.getName(), "name", "id", id)) {
				throw new DuplicateEmailTemplateNameException(emailTemplate.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void create(EmailTemplate emailTemplate) throws Exception {
		Locale locale = emailTemplate.getLocale();
		if (locale != null) {
			emailTemplate.setLocale(new EntityManagerHelper(em).find(Locale.class, locale));
		}
		super.create(emailTemplate);
	}

	@Override
	public void update(EmailTemplate emailTemplate) throws Exception {
		Locale locale = emailTemplate.getLocale();
		if (locale != null) {
			emailTemplate.setLocale(new EntityManagerHelper(em).find(Locale.class, locale));
		}
		super.update(emailTemplate);
	}

}
