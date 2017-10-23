package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.service.exceptions.DuplicateVendorAccountNumberException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class VendorServiceImpl extends ServiceImpl<Vendor> implements VendorService {

	public VendorServiceImpl() {
	}

	@Override
	protected void validate(Vendor vendor, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "accountNumber", vendor.getAccountNumber(), vendor.getId(),
					operation)) {
				throw new DuplicateVendorAccountNumberException(vendor.getAccountNumber());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
