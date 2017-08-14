package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Vendor;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class VendorServiceImpl extends RainbowAssetExplorerServiceImpl<Vendor, Long, SearchOptions> {

	public VendorServiceImpl(Dao<Vendor, Long, SearchOptions> dao) {
		super(dao);
	}
}
