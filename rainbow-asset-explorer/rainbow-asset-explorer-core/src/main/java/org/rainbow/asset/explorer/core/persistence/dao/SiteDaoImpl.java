/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Site;
import org.rainbow.asset.explorer.core.entities.SiteStatus;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateSiteNameException;
import org.rainbow.asset.explorer.core.persistence.exceptions.SiteCommissioningDateNullException;
import org.rainbow.asset.explorer.core.persistence.exceptions.SiteDecommissioningDateNullException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.UpdateOperation;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class SiteDaoImpl extends TrackableDaoImpl<Site, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SiteDaoImpl() {
		super(Site.class);
	}

	@Override
	protected void validate(Site site, UpdateOperation operation) {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = site.getId();
			validateStatus(site);
			if (helper.isDuplicate(Site.class, site.getName(), "name", "id", id)) {
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