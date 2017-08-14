package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.ShipMethod;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class ShipMethodServiceImpl extends RainbowAssetExplorerServiceImpl<ShipMethod, Long, SearchOptions> {

	public ShipMethodServiceImpl(Dao<ShipMethod, Long, SearchOptions> dao) {
		super(dao);
	}
}
