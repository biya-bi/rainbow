package org.rainbow.journal.core.persistence.dao;

import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

import org.rainbow.core.persistence.DaoImpl;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;
import org.rainbow.journal.core.entities.File;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.persistence.exceptions.DuplicateProfileUserNameException;
import org.rainbow.journal.core.utilities.PersistenceSettings;

@Pageable(attributeName = "id")
public class ProfileDao extends DaoImpl<Profile, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public ProfileDao() {
		super(Profile.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Profile profile, UpdateOperation operation) throws DuplicateProfileUserNameException {
		switch (operation) {
		case CREATE:
			if (exists(profile.getUserName(), "userName", null, null)) {
				throw new DuplicateProfileUserNameException(profile.getUserName());
			}
			break;
		case UPDATE:
			if (exists(profile.getUserName(), "userName", "id", profile.getId())) {
				throw new DuplicateProfileUserNameException(profile.getUserName());
			}
		case DELETE:
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Profile entity) throws Exception {
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
