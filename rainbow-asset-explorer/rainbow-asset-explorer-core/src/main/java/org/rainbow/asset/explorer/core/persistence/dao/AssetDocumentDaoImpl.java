/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.AssetDocument;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Pageable;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class AssetDocumentDaoImpl extends TrackableDaoImpl<AssetDocument, Long> {
	
	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AssetDocumentDaoImpl() {
		super(AssetDocument.class);
	}
}
