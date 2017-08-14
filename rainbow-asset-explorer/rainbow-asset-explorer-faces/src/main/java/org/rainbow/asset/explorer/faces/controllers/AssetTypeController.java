/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.core.entities.AssetType;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateAssetTypeNameException;
import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.asset.explorer.faces.utilities.JsfUtil;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "AssetTypeCreated", updatedMessageKey = "AssetTypeUpdated", deletedMessageKey = "AssetTypeDeleted")
public class AssetTypeController extends TrackableController<AssetType, Long, SearchOptions> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 173159477628876784L;

	private static final String DUPLICATE_ASSET_TYPE_NAME_ERROR_KEY = "DuplicateAssetTypeName";

	@Autowired
	@Qualifier("assetTypeService")
	private Service<AssetType, Long, SearchOptions> service;

	public AssetTypeController() {
		super(AssetType.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateAssetTypeNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateAssetTypeNameException e = (DuplicateAssetTypeNameException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_ASSET_TYPE_NAME_ERROR_KEY),
					e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected Service<AssetType, Long, SearchOptions> getService() {
		return service;
	}
}
