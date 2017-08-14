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

import org.rainbow.asset.explorer.core.entities.Manufacturer;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateManufacturerNameException;
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
@CrudNotificationInfo(createdMessageKey = "ManufacturerCreated", updatedMessageKey = "ManufacturerUpdated", deletedMessageKey = "ManufacturerDeleted")
public class ManufacturerController extends TrackableController<Manufacturer, Long, SearchOptions>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5007883595223894537L;

	private static final String DUPLICATE_MANUFACTURER_NAME_ERROR_KEY = "DuplicateManufacturerName";

	@Autowired
	@Qualifier("manufacturerService")
	private Service<Manufacturer, Long, SearchOptions> service;
	
	public ManufacturerController() {
		super(Manufacturer.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateManufacturerNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateManufacturerNameException e = (DuplicateManufacturerNameException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_MANUFACTURER_NAME_ERROR_KEY),
					e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected Service<Manufacturer, Long, SearchOptions> getService() {
		return service;
	}
}
