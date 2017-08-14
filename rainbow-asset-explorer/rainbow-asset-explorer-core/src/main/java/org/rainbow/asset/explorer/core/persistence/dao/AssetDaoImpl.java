/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.core.entities.Asset;
import org.rainbow.asset.explorer.core.entities.AssetType;
import org.rainbow.asset.explorer.core.entities.Currency;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.entities.Site;
import org.rainbow.asset.explorer.core.entities.Vendor;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateAssetSerialNumberException;
import org.rainbow.asset.explorer.core.utilities.EntityManagerHelper;
import org.rainbow.asset.explorer.core.utilities.PersistenceSettings;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.UpdateOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class AssetDaoImpl extends TrackableDaoImpl<Asset, Long> {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Autowired
	@Qualifier("productDao")
	private Dao<Product, Long, SearchOptions> productDao;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AssetDaoImpl() {
		super(Asset.class);
	}

	@Override
	protected void validate(Asset asset, UpdateOperation operation) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		Long id;
		switch (operation) {
		case CREATE:
			id = null;
		case UPDATE:
			id = asset.getId();
			if (helper.isDuplicate(Asset.class, asset.getSerialNumber(), "serialNumber", "id", id)) {
				throw new DuplicateAssetSerialNumberException(asset.getSerialNumber());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

	private void setAssociations(Asset asset) throws Exception {
		EntityManagerHelper helper = new EntityManagerHelper(em);
		if (asset.getProduct() != null) {
			asset.setProduct(helper.find(Product.class, asset.getProduct()));
		}
		if (asset.getAssetType() != null) {
			asset.setAssetType(helper.find(AssetType.class, asset.getAssetType()));
		}
		if (asset.getPurchaseCurrency() != null) {
			asset.setPurchaseCurrency(helper.find(Currency.class, asset.getPurchaseCurrency()));
		}
		if (asset.getSite() != null) {
			asset.setSite(helper.find(Site.class, asset.getSite()));
		}
		if (asset.getVendor() != null) {
			asset.setVendor(helper.find(Vendor.class, asset.getVendor()));
		}
	}

	@Override
	public void create(Asset entity) throws Exception {
		setAssociations(entity);
		super.create(entity);
	}

	@Override
	public void update(Asset entity) throws Exception {
		setAssociations(entity);
		super.update(entity);
	}
}
