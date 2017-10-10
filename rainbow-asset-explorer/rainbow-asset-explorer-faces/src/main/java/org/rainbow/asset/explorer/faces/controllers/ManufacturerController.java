package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.CrudNotificationInfo;
import org.rainbow.asset.explorer.orm.entities.Manufacturer;
import org.rainbow.asset.explorer.service.exceptions.DuplicateManufacturerNameException;
import org.rainbow.asset.explorer.service.services.ManufacturerService;
import org.rainbow.faces.util.FacesContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "ManufacturerCreated", updatedMessageKey = "ManufacturerUpdated", deletedMessageKey = "ManufacturerDeleted")
public class ManufacturerController extends AuditableController<Manufacturer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5007883595223894537L;

	private static final String DUPLICATE_MANUFACTURER_NAME_ERROR_KEY = "DuplicateManufacturerName";

	@Autowired
	private ManufacturerService service;
	
	public ManufacturerController() {
		super(Manufacturer.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateManufacturerNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateManufacturerNameException e = (DuplicateManufacturerNameException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_MANUFACTURER_NAME_ERROR_KEY),
					e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected ManufacturerService getService() {
		return service;
	}
}
