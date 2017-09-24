package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.AssetType;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAssetTypeNameException;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class AssetTypeServiceImpl extends ServiceImpl<AssetType, Long, SearchOptions> {

	public AssetTypeServiceImpl() {
	}

	@Override
	protected void validate(AssetType assetType, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", assetType.getName(), assetType.getId(), operation)) {
				throw new DuplicateAssetTypeNameException(assetType.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
