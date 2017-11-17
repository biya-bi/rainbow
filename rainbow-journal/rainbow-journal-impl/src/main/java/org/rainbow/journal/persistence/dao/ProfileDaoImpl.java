package org.rainbow.journal.persistence.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.util.PersistenceSettings;
import org.rainbow.persistence.dao.DaoImpl;
import org.rainbow.persistence.dao.Pageable;

@Pageable(attributeName = "id")
public class ProfileDaoImpl extends DaoImpl<Profile> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public ProfileDaoImpl() {
		super(Profile.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void merge(Profile entity)  {
		super.merge(entity);
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
				getEntityManager().persist(entity.getPhoto());
			else
				getEntityManager().merge(entity.getPhoto());
		}
	}
}
