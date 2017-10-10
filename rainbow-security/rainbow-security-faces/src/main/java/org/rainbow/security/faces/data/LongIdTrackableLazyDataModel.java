package org.rainbow.security.faces.data;

import org.rainbow.orm.entities.Trackable;

public abstract class LongIdTrackableLazyDataModel<TModel extends Trackable<Long>>
		extends TrackableLazyDataModel<TModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4527984494404862322L;

	@Override
	protected Object convert(String rowKey) {
		return Long.valueOf(rowKey);
	}
}
