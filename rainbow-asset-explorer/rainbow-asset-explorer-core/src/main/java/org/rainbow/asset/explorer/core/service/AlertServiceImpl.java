package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Alert;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class AlertServiceImpl extends RainbowAssetExplorerServiceImpl<Alert, Integer, SearchOptions> {

	public AlertServiceImpl(Dao<Alert, Integer, SearchOptions> dao) {
		super(dao);
	}

}
