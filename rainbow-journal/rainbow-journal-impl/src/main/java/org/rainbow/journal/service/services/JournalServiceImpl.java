package org.rainbow.journal.service.services;

import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.service.exceptions.DuplicateJournalException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class JournalServiceImpl extends ServiceImpl<Journal> implements JournalService {

	public JournalServiceImpl() {
	}

	@Override
	protected void validate(Journal journal, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", journal.getName(), journal.getId(), operation)) {
				throw new DuplicateJournalException(journal.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
