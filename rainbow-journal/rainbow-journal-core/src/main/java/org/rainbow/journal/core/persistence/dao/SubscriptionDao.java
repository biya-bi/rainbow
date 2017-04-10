package org.rainbow.journal.core.persistence.dao;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.entities.Subscription;
import org.rainbow.journal.core.persistence.exceptions.DuplicateSubscriptionException;
import org.rainbow.journal.core.utilities.EntityManagerHelper;
import org.rainbow.journal.core.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class SubscriptionDao extends DaoImpl<Subscription, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public SubscriptionDao() {
		super(Subscription.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Subscription subscription, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Profile persistentProfile;
		Journal persistentJournal;
		Long subscriptionId;
		switch (operation) {
		case CREATE:
			subscriptionId = null;
		case UPDATE:
			subscriptionId = subscription.getId();
			persistentJournal = helper.getReference(Journal.class, subscription.getJournal().getId());
			subscription.setJournal(persistentJournal);
			persistentProfile = helper.getReference(Profile.class, subscription.getSubscriberProfile().getId());
			subscription.setSubscriberProfile(persistentProfile);
			Map<String, Object> pathValuePairs = new HashMap<>();
			pathValuePairs.put("journal.id", persistentJournal.getId());
			pathValuePairs.put("subscriberProfile.id", persistentProfile.getId());
			if (helper.isDuplicate(Subscription.class, pathValuePairs, "id", subscriptionId)) {
				throw new DuplicateSubscriptionException(persistentProfile.getUserName(), persistentJournal.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
