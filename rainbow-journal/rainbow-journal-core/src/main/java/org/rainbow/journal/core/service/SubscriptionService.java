package org.rainbow.journal.core.service;

import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.journal.core.entities.Subscription;
import org.rainbow.persistence.Dao;

public class SubscriptionService extends RainbowJournalService<Subscription, Long, SearchOptions> {

	public SubscriptionService(Dao<Subscription, Long, SearchOptions> dao) {
		super(dao);
	}
}
