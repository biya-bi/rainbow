package org.rainbow.orm.listeners;

import java.util.Date;
import java.util.Objects;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.rainbow.orm.entities.AbstractAuditableEntity;

public abstract class AbstractAuditableEntityListener {

	@PrePersist
	public void prePersist(Object obj) {
		checkEntity(obj);
		final AbstractAuditableEntity<?> entity = (AbstractAuditableEntity<?>) obj;
		final Date now = new Date();
		entity.setCreationDate(now);
		entity.setLastUpdateDate(now);

		final String userName = getUserName();
		entity.setCreator(userName);
		entity.setUpdater(userName);
	}

	@PreUpdate
	public void preUpdate(Object obj) {
		checkEntity(obj);
		preUpdateOrRemove(obj);
	}

	@PreRemove
	public void preRemove(Object obj) {
		checkEntity(obj);
		preUpdateOrRemove(obj);
	}

	private void preUpdateOrRemove(Object obj) {
		final AbstractAuditableEntity<?> entity = (AbstractAuditableEntity<?>) obj;
		final Date now = new Date();
		entity.setLastUpdateDate(now);

		final String userName = getUserName();
		entity.setUpdater(userName);
	}

	protected abstract String getUserName();

	private void checkEntity(Object obj) {
		Objects.requireNonNull(obj);
		if (!AbstractAuditableEntity.class.isAssignableFrom(obj.getClass())) {
			throw new IllegalStateException(String.format("The entity to be audited must be a subclass of %s.",
					AbstractAuditableEntity.class.getName()));
		}
	}
}
