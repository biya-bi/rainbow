package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.SiteDocument;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class SiteDocumentServiceImpl extends RainbowAssetExplorerServiceImpl<SiteDocument, Long, SearchOptions> {

	public SiteDocumentServiceImpl(Dao<SiteDocument, Long, SearchOptions> dao) {
		super(dao);
	}
}
