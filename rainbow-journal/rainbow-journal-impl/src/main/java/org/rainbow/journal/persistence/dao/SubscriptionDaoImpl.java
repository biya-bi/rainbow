package org.rainbow.journal.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.orm.entities.Subscription;
import org.rainbow.journal.util.PersistenceSettings;
import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.util.EntityManagerUtil;

@Pageable(attributeName = "id")
public class SubscriptionDaoImpl extends DaoImpl<Subscription> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public SubscriptionDaoImpl() {
		super(Subscription.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void onCreate(Subscription subscription) throws Exception {
		super.onCreate(subscription);
		fixAssociations(subscription);
	}

	@Override
	protected void onUpdate(Subscription subscription) throws Exception {
		super.onUpdate(subscription);
		fixAssociations(subscription);
	}

	private void fixAssociations(Subscription subscription) throws Exception {
		if (subscription.getJournal() != null) {
			subscription.setJournal(
					EntityManagerUtil.find(this.getEntityManager(), Journal.class, subscription.getJournal()));
		}
		if (subscription.getSubscriberProfile() != null) {
			subscription.setSubscriberProfile(EntityManagerUtil.find(this.getEntityManager(), Profile.class,
					subscription.getSubscriberProfile()));
		}
	}
}
