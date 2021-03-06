/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.lazy;

import org.rainbow.core.entities.Trackable;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class LongIdTrackableLazyDataModel<TEntity extends Trackable<?>> extends TrackableLazyDataModel<TEntity, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8681932785449074563L;

	@Override
    protected Long toModelId(String rowKey) {
        return Long.valueOf(rowKey);
    }
}
