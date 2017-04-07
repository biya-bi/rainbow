package org.rainbow.journal.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.journal.core.entities.Journal;

public class JournalService extends RainbowJournalService<Journal, Long, SearchOptions> {

	public JournalService(Dao<Journal, Long, SearchOptions> dao) {
		super(dao);
	}
}
