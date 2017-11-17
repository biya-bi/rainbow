package org.rainbow.journal.service.services;

import java.util.HashMap;
import java.util.Map;

import org.rainbow.journal.orm.entities.Subscription;
import org.rainbow.journal.service.exceptions.DuplicateSubscriptionException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class SubscriptionServiceImpl extends ServiceImpl<Subscription> implements SubscriptionService {

	public SubscriptionServiceImpl() {
	}

	@Override
	protected void validate(Subscription subscription, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> filters = new HashMap<>();
			filters.put("journal.id", subscription.getJournal().getId());
			filters.put("subscriberProfile.id", subscription.getSubscriberProfile().getId());
			if (DaoUtil.isDuplicate(this.getDao(), filters, subscription.getId(), operation)) {
				throw new DuplicateSubscriptionException(subscription.getSubscriberProfile().getId(),
						subscription.getJournal().getId());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
