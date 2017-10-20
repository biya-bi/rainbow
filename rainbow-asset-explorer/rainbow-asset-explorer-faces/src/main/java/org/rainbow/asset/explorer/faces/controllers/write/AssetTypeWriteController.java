package org.rainbow.asset.explorer.faces.controllers.write;

import static org.rainbow.asset.explorer.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.AssetType;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAssetTypeNameException;
import org.rainbow.asset.explorer.service.services.AssetTypeService;
import org.rainbow.faces.controllers.write.AbstractWriteController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "AssetTypeCreated", updatedMessageKey = "AssetTypeUpdated", deletedMessageKey = "AssetTypeDeleted")
public class AssetTypeWriteController extends AbstractWriteController<AssetType> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 173159477628876784L;

	private static final String DUPLICATE_ASSET_TYPE_NAME_ERROR_KEY = "DuplicateAssetTypeName";

	@Autowired
	private AssetTypeService service;

	public AssetTypeWriteController() {
		super(AssetType.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateAssetTypeNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateAssetTypeNameException e = (DuplicateAssetTypeNameException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_ASSET_TYPE_NAME_ERROR_KEY),
					e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	public Service<AssetType> getService() {
		return service;
	}
}
