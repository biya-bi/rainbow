package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Site;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class SiteServiceImpl extends RainbowAssetExplorerServiceImpl<Site, Long, SearchOptions> {

	public SiteServiceImpl(Dao<Site, Long, SearchOptions> dao) {
		super(dao);
	}
}
