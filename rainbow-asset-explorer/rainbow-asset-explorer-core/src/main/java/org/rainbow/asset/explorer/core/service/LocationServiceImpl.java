package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Location;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class LocationServiceImpl extends RainbowAssetExplorerServiceImpl<Location, Long, SearchOptions> {

	public LocationServiceImpl(Dao<Location, Long, SearchOptions> dao) {
		super(dao);
	}
}
