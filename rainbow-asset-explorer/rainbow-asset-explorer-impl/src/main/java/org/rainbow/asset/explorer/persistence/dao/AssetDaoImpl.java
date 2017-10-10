package org.rainbow.asset.explorer.persistence.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.rainbow.asset.explorer.orm.entities.Asset;
import org.rainbow.asset.explorer.orm.entities.AssetType;
import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.Site;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.utilities.PersistenceSettings;
import org.rainbow.persistence.dao.Pageable;
import org.rainbow.util.EntityManagerUtil;

/**
 *
 * @author Biya-Bi
 */
@Pageable(attributeName = "id")
public class AssetDaoImpl extends TrackableDaoImpl<Asset> implements AssetDao {

	@PersistenceContext(unitName = PersistenceSettings.PERSISTENCE_UNIT_NAME)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AssetDaoImpl() {
		super(Asset.class);
	}

	private void fixAssociations(Asset asset) throws Exception {
		if (asset.getProduct() != null) {
			asset.setProduct(EntityManagerUtil.find(this.getEntityManager(), Product.class, asset.getProduct()));
		}
		if (asset.getAssetType() != null) {
			asset.setAssetType(EntityManagerUtil.find(this.getEntityManager(), AssetType.class, asset.getAssetType()));
		}
		if (asset.getPurchaseCurrency() != null) {
			asset.setPurchaseCurrency(
					EntityManagerUtil.find(this.getEntityManager(), Currency.class, asset.getPurchaseCurrency()));
		}
		if (asset.getSite() != null) {
			asset.setSite(EntityManagerUtil.find(this.getEntityManager(), Site.class, asset.getSite()));
		}
		if (asset.getVendor() != null) {
			asset.setVendor(EntityManagerUtil.find(this.getEntityManager(), Vendor.class, asset.getVendor()));
		}
	}

	@Override
	protected void onCreate(Asset asset) throws Exception {
		super.onCreate(asset);
		fixAssociations(asset);
	}

	@Override
	protected void onUpdate(Asset asset) throws Exception {
		super.onUpdate(asset);
		fixAssociations(asset);
	}
}
