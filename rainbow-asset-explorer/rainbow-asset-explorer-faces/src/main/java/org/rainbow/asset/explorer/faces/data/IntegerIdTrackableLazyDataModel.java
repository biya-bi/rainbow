package org.rainbow.asset.explorer.faces.data;

import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 */
public abstract class IntegerIdTrackableLazyDataModel<TModel extends Trackable<?>> extends TrackableLazyDataModel<TModel, Integer> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8524247059960772349L;

	@Override
    protected Integer toModelId(String rowKey) {
        return Integer.valueOf(rowKey);
    }
}
