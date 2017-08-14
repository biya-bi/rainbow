/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import java.io.Serializable;

import org.rainbow.asset.explorer.core.entities.Trackable;
import org.rainbow.core.persistence.DaoImpl;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class TrackableDaoImpl<TEntity extends Trackable<?>, TKey extends Serializable> extends DaoImpl<TEntity, TKey> {

	public TrackableDaoImpl(Class<TEntity> entityClass) {
		super(entityClass);
	}

	@Override
	protected void onDelete(TEntity entity, TEntity persistentEntity) {
		persistentEntity.setUpdater(entity.getUpdater());
		persistentEntity.setLastUpdateDate(entity.getLastUpdateDate());
	}
}
