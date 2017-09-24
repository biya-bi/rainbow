package org.rainbow.journal.core.service;

import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.journal.core.entities.Publication;
import org.rainbow.persistence.Dao;

public class PublicationService extends RainbowJournalService<Publication, Long, SearchOptions> {

	public PublicationService(Dao<Publication, Long, SearchOptions> dao) {
		super(dao);
	}
}
