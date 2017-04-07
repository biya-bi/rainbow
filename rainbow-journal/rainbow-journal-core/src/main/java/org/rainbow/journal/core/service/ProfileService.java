package org.rainbow.journal.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.journal.core.entities.Profile;

public class ProfileService extends RainbowJournalService<Profile, Long, SearchOptions> {

	public ProfileService(Dao<Profile, Long, SearchOptions> dao) {
		super(dao);
	}
}
