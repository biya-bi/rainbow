package org.rainbow.asset.explorer.persistence.dao;

import org.rainbow.orm.entities.Trackable;
import org.rainbow.persistence.dao.DaoImpl;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class TrackableDaoImpl<TEntity extends Trackable<?>>
		extends DaoImpl<TEntity> {

	public TrackableDaoImpl(Class<TEntity> entityClass) {
		super(entityClass);
	}

	@Override
	protected void onDelete(TEntity entity, TEntity persistentEntity) {
		persistentEntity.setUpdater(entity.getUpdater());
		persistentEntity.setLastUpdateDate(entity.getLastUpdateDate());
	}
}
