package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.Pageable;
import org.rainbow.utilities.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class EmailTemplateDaoImpl extends TrackableDaoImpl<EmailTemplate, Integer> {

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
	protected void onCreate(EmailTemplate emailTemplate) throws Exception {
		super.onCreate(emailTemplate);
		fixAssociations(emailTemplate);
	}

	@Override
	protected void onUpdate(EmailTemplate emailTemplate) throws Exception {
		super.onUpdate(emailTemplate);
		fixAssociations(emailTemplate);
	}

	private void fixAssociations(EmailTemplate emailTemplate) {
		Locale locale = emailTemplate.getLocale();
		if (locale != null) {
			emailTemplate.setLocale(EntityManagerUtil.find(this.getEntityManager(), Locale.class, locale));
		}
	}
}
