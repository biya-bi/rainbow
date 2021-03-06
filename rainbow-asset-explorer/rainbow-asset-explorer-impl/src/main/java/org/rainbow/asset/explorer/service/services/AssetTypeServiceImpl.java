package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.AssetType;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAssetTypeNameException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class AssetTypeServiceImpl extends ServiceImpl<AssetType> implements AssetTypeService {

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
