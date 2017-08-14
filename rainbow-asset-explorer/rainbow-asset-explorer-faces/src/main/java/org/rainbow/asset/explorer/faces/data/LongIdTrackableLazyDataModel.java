/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.data;

import org.rainbow.asset.explorer.core.entities.Trackable;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 */
public abstract class LongIdTrackableLazyDataModel<TModel extends Trackable<?>> extends TrackableLazyDataModel<TModel, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8681932785449074563L;

	@Override
    protected Long toModelId(String rowKey) {
        return Long.valueOf(rowKey);
    }
}
