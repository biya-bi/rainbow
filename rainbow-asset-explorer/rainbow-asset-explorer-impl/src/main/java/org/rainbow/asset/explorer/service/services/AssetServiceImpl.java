package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Asset;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAssetSerialNumberException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class AssetServiceImpl extends ServiceImpl<Asset> implements AssetService {

	public AssetServiceImpl() {
	}

	@Override
	protected void validate(Asset asset, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "serialNumber", asset.getSerialNumber(), asset.getId(), operation)) {
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
