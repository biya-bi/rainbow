package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Asset;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class AssetServiceImpl extends RainbowAssetExplorerServiceImpl<Asset, Long, SearchOptions> {

	public AssetServiceImpl(Dao<Asset, Long, SearchOptions> dao) {
		super(dao);
	}
}
