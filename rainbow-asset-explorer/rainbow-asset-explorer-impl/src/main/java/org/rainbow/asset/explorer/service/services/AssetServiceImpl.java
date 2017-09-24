package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Asset;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAssetSerialNumberException;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class AssetServiceImpl extends ServiceImpl<Asset, Long, SearchOptions> {

	public AssetServiceImpl() {
	}

	@Override
	protected void validate(Asset asset, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "serialNumber", asset.getSerialNumber(), asset.getId(),
					operation)) {
				throw new DuplicateAssetSerialNumberException(asset.getSerialNumber());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}