package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Manufacturer;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class ManufacturerServiceImpl extends RainbowAssetExplorerServiceImpl<Manufacturer, Long, SearchOptions> {

	public ManufacturerServiceImpl(Dao<Manufacturer, Long, SearchOptions> dao) {
		super(dao);
	}
}
