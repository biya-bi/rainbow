package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Currency;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class CurrencyServiceImpl extends RainbowAssetExplorerServiceImpl<Currency, Integer, SearchOptions> {

	public CurrencyServiceImpl(Dao<Currency, Integer, SearchOptions> dao) {
		super(dao);
	}
}
