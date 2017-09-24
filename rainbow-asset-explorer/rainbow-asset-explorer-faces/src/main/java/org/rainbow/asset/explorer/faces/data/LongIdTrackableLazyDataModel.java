package org.rainbow.asset.explorer.faces.data;

import org.rainbow.orm.entities.Trackable;

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
