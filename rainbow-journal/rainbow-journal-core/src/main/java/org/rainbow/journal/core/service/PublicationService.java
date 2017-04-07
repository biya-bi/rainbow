package org.rainbow.journal.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.journal.core.entities.Publication;

public class PublicationService extends RainbowJournalService<Publication, Long, SearchOptions> {

	public PublicationService(Dao<Publication, Long, SearchOptions> dao) {
		super(dao);
	}
}
