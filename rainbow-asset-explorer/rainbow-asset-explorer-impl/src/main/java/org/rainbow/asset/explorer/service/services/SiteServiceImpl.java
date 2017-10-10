package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Site;
import org.rainbow.asset.explorer.orm.entities.SiteStatus;
import org.rainbow.asset.explorer.service.exceptions.DuplicateSiteNameException;
import org.rainbow.asset.explorer.service.exceptions.SiteCommissioningDateNullException;
import org.rainbow.asset.explorer.service.exceptions.SiteDecommissioningDateNullException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class SiteServiceImpl extends ServiceImpl<Site> implements SiteService {

	public SiteServiceImpl() {
	}

	@Override
	protected void validate(Site site, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			validateStatus(site);
			if (DaoUtil.isDuplicate(this.getDao(), "name", site.getName(), site.getId(), operation)) {
				throw new DuplicateSiteNameException(site.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	private void validateStatus(Site site)
			throws SiteCommissioningDateNullException, SiteDecommissioningDateNullException {
		if (site.getStatus() == SiteStatus.ACTIVE) {
			if (site.getDateOfCommissioning() == null) {
				throw new SiteCommissioningDateNullException();
			}
		} else if (site.getStatus() == SiteStatus.DECOMMISSIONED) {
			if (site.getDateOfDecommissioning() == null) {
				throw new SiteDecommissioningDateNullException();
			}
		}
	}
}
