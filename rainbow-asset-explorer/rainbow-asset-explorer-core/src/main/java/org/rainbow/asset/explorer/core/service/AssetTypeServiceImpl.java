package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.AssetType;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class AssetTypeServiceImpl extends RainbowAssetExplorerServiceImpl<AssetType, Long, SearchOptions> {

	public AssetTypeServiceImpl(Dao<AssetType, Long, SearchOptions> dao) {
		super(dao);
	}
}
