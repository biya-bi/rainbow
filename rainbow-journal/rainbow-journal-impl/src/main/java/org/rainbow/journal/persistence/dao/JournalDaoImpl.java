package org.rainbow.journal.persistence.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.util.PersistenceSettings;
import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.util.EntityManagerUtil;

@Pageable(attributeName = "id")
public class JournalDaoImpl extends DaoImpl<Journal> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public JournalDaoImpl() {
		super(Journal.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void merge(Journal entity) {
		super.merge(entity);
		if (entity.getPhoto() != null) {
			boolean photoExists = false;
			if (entity.getPhoto().getId() != null) {
				try {
					File persistentPhoto = getEntityManager().getReference(File.class, entity.getPhoto().getId());
					if (persistentPhoto != null && persistentPhoto.getId().equals(entity.getPhoto().getId()))
						photoExists = true;
				} catch (EntityNotFoundException e) {
					Logger.getLogger(this.getClass().getName()).warning(entity.getPhoto() + " was not found.");
				}
			}
			if (!photoExists)
				getEntityManager().persist(entity.getPhoto());
			else
				getEntityManager().merge(entity.getPhoto());
		}
	}

	@Override
	protected void onCreate(Journal journal) throws Exception {
		super.onCreate(journal);
		fixAssociations(journal);
	}

	@Override
	protected void onUpdate(Journal journal) throws Exception {
		super.onUpdate(journal);
		fixAssociations(journal);
	}

	private void fixAssociations(Journal journal) throws Exception {
		if (journal.getOwnerProfile() != null) {
			journal.setOwnerProfile(
					EntityManagerUtil.find(this.getEntityManager(), Profile.class, journal.getOwnerProfile()));
		}
	}
}
