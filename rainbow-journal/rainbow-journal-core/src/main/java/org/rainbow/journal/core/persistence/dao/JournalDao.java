package org.rainbow.journal.core.persistence.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;
import org.rainbow.journal.core.entities.File;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.persistence.exceptions.DuplicateJournalException;
import org.rainbow.journal.core.utilities.EntityManagerHelper;
import org.rainbow.journal.core.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class JournalDao extends DaoImpl<Journal, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public JournalDao() {
		super(Journal.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Journal journal, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Profile persistentProfile;
		Long journalId;
		switch (operation) {
		case CREATE:
			journalId = null;
		case UPDATE:
			journalId = journal.getId();
			persistentProfile = helper.getReference(Profile.class, journal.getOwnerProfile().getId());
			journal.setOwnerProfile(persistentProfile);
			if (helper.isDuplicate(Journal.class, journal.getName(), "name", "id", journalId)) {
				throw new DuplicateJournalException(journal.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Journal entity) throws Exception {
		super.update(entity);
		if (entity.getPhoto() != null) {
			boolean photoExists = false;
			if (entity.getPhoto().getId() != null) {
				try {
					File persistentPhoto = em.getReference(File.class, entity.getPhoto().getId());
					if (persistentPhoto != null && persistentPhoto.getId().equals(entity.getPhoto().getId()))
						photoExists = true;
				} catch (EntityNotFoundException e) {
					Logger.getLogger(this.getClass().getName()).warning(entity.getPhoto() + " was not found.");
				}
			}
			if (!photoExists)
				em.persist(entity.getPhoto());
			else
				em.merge(entity.getPhoto());
		}
	}
}
