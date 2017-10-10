package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocationNameException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class LocationServiceImpl extends ServiceImpl<Location> implements LocationService {

	public LocationServiceImpl() {
	}

	@Override
	protected void validate(Location location, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", location.getName(), location.getId(), operation)) {
				throw new DuplicateLocationNameException(location.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
