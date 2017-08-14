package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Locale;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class LocaleServiceImpl extends RainbowAssetExplorerServiceImpl<Locale, Integer, SearchOptions> {

	public LocaleServiceImpl(Dao<Locale, Integer, SearchOptions> dao) {
		super(dao);
	}
}
