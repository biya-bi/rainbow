package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Manufacturer;
import org.rainbow.asset.explorer.service.exceptions.DuplicateManufacturerNameException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class ManufacturerServiceImpl extends ServiceImpl<Manufacturer> implements ManufacturerService {

	public ManufacturerServiceImpl() {
	}

	@Override
	protected void validate(Manufacturer manufacturer, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", manufacturer.getName(), manufacturer.getId(), operation)) {
				throw new DuplicateManufacturerNameException(manufacturer.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
