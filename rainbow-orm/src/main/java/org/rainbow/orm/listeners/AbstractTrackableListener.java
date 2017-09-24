package org.rainbow.orm.listeners;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import org.rainbow.orm.entities.Trackable;

public abstract class AbstractTrackableListener {

	@PrePersist
	public void prePersist(Object obj) {
		final Trackable<?> trackable = (Trackable<?>) obj;
		final Date now = new Date();
		trackable.setCreationDate(now);
		trackable.setLastUpdateDate(now);

		final String userName = getUserName();
		trackable.setCreator(userName);
		trackable.setUpdater(userName);
	}

	@PreUpdate
	public void preUpdate(Object obj) {
		preUpdateOrRemove(obj);
	}

	@PreRemove
	public void preRemove(Object obj) {
		preUpdateOrRemove(obj);
	}

	private void preUpdateOrRemove(Object obj) {
		final Trackable<?> trackable = (Trackable<?>) obj;
		final Date now = new Date();
		trackable.setLastUpdateDate(now);

		final String userName = getUserName();
		trackable.setUpdater(userName);
	}

	protected abstract String getUserName();
}
