package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.service.exceptions.DuplicateShipMethodNameException;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class ShipMethodServiceImpl extends ServiceImpl<ShipMethod, Long, SearchOptions> {

	public ShipMethodServiceImpl() {		
	}

	@Override
	protected void validate(ShipMethod shipMethod, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", shipMethod.getName(), shipMethod.getId(), operation)) {
				throw new DuplicateShipMethodNameException(shipMethod.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
