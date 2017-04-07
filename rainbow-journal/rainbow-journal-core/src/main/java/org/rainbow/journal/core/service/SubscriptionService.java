package org.rainbow.journal.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.journal.core.entities.Subscription;

public class SubscriptionService extends RainbowJournalService<Subscription, Long, SearchOptions> {

	public SubscriptionService(Dao<Subscription, Long, SearchOptions> dao) {
		super(dao);
	}
}
