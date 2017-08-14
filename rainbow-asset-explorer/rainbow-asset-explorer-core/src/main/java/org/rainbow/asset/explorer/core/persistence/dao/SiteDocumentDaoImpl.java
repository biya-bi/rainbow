/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.SiteDocument;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class SiteDocumentDaoImpl extends TrackableDaoImpl<SiteDocument, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SiteDocumentDaoImpl() {
		super(SiteDocument.class);
	}
}