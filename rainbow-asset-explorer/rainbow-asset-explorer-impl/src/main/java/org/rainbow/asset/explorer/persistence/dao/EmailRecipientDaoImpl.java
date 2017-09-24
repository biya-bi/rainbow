package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.Pageable;
import org.rainbow.utilities.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class EmailRecipientDaoImpl extends TrackableDaoImpl<EmailRecipient, Integer> {

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
	protected void onCreate(EmailRecipient emailRecipient) throws Exception {
		super.onCreate(emailRecipient);
		fixAssociations(emailRecipient);
	}

	@Override
	protected void onUpdate(EmailRecipient emailRecipient) throws Exception {
		super.onUpdate(emailRecipient);
		fixAssociations(emailRecipient);
	}

	private void fixAssociations(EmailRecipient emailRecipient) {
		Locale locale = emailRecipient.getLocale();
		if (locale != null) {
			emailRecipient.setLocale(EntityManagerUtil.find(this.getEntityManager(), Locale.class, locale));
		}
	}
}