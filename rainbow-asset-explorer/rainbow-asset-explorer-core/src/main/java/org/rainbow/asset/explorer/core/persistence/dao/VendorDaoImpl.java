package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Vendor;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateVendorAccountNumberException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

@Pageable(attributeName = "id")
public class VendorDaoImpl extends BusinessEntityDao<Vendor, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	public VendorDaoImpl() {
		super(Vendor.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	protected void validate(Vendor vendor, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = vendor.getId();
			if (helper.isDuplicate(Vendor.class, vendor.getAccountNumber(), "accountNumber", "id", id)) {
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
