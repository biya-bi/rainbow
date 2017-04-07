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
import org.rainbow.journal.core.entities.Publication;
import org.rainbow.journal.core.utilities.EntityManagerHelper;
import org.rainbow.journal.core.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class PublicationDao extends DaoImpl<Publication, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public PublicationDao() {
		super(Publication.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Publication publication, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Profile persistentProfile;
		Journal persistentJournal;
		switch (operation) {
		case CREATE:
		case UPDATE:
			persistentJournal = helper.getReference(Journal.class, publication.getJournal().getId());
			publication.setJournal(persistentJournal);
			persistentProfile = helper.getReference(Profile.class, publication.getPublisherProfile().getId());
			publication.setPublisherProfile(persistentProfile);
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Publication entity) throws Exception {
		super.update(entity);
		if (entity.getFile() != null) {
			boolean fileExists = false;
			if (entity.getFile().getId() != null) {
				try {
					File persistentPhoto = em.getReference(File.class, entity.getFile().getId());
					if (persistentPhoto != null && persistentPhoto.getId().equals(entity.getFile().getId()))
						fileExists = true;
				} catch (EntityNotFoundException e) {
					Logger.getLogger(this.getClass().getName()).warning(entity.getFile() + " was not found.");
				}
			}
			if (!fileExists)
				em.persist(entity.getFile());
			else
				em.merge(entity.getFile());
		}
	}
}
