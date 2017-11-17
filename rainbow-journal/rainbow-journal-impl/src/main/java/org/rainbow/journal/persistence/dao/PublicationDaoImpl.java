package org.rainbow.journal.persistence.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.util.PersistenceSettings;
import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.util.EntityManagerUtil;

@Pageable(attributeName = "id")
public class PublicationDaoImpl extends DaoImpl<Publication> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public PublicationDaoImpl() {
		super(Publication.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void onCreate(Publication publication) throws Exception {
		super.onCreate(publication);
		fixAssociations(publication);
	}

	@Override
	protected void onUpdate(Publication publication) throws Exception {
		super.onUpdate(publication);
		fixAssociations(publication);
	}

	@Override
	protected void merge(Publication publication) {
		super.merge(publication);
		if (publication.getFile() != null) {
			boolean fileExists = false;
			if (publication.getFile().getId() != null) {
				try {
					File persistentPhoto = getEntityManager().getReference(File.class, publication.getFile().getId());
					if (persistentPhoto != null && persistentPhoto.getId().equals(publication.getFile().getId()))
						fileExists = true;
				} catch (EntityNotFoundException e) {
					Logger.getLogger(this.getClass().getName()).warning(publication.getFile() + " was not found.");
				}
			}
			if (!fileExists)
				getEntityManager().persist(publication.getFile());
			else
				getEntityManager().merge(publication.getFile());
		}
	}

	private void fixAssociations(Publication publication) throws Exception {
		if (publication.getJournal() != null) {
			publication.setJournal(
					EntityManagerUtil.find(this.getEntityManager(), Journal.class, publication.getJournal()));
		}
		if (publication.getPublisherProfile() != null) {
			publication.setPublisherProfile(
					EntityManagerUtil.find(this.getEntityManager(), Profile.class, publication.getPublisherProfile()));
		}
	}

}
