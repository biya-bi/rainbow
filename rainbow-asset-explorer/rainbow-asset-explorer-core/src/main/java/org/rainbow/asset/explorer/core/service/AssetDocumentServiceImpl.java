package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.AssetDocument;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class AssetDocumentServiceImpl extends RainbowAssetExplorerServiceImpl<AssetDocument, Long, SearchOptions> {
	
	public AssetDocumentServiceImpl(Dao<AssetDocument, Long, SearchOptions> dao) {
		super(dao);
	}
}
