package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.CrudNotificationInfo;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocationNameException;
import org.rainbow.asset.explorer.service.services.LocationService;
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
@CrudNotificationInfo(createdMessageKey = "LocationCreated", updatedMessageKey = "LocationUpdated", deletedMessageKey = "LocationDeleted")
public class LocationController extends AuditableController<Location> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2981018316085136412L;

	private static final String DUPLICATE_LOCATION_NAME_ERROR_KEY = "DuplicateLocationName";

	@Autowired
	private LocationService service;

	public LocationController() {
		super(Location.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateLocationNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateLocationNameException e = (DuplicateLocationNameException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_LOCATION_NAME_ERROR_KEY), e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected LocationService getService() {
		return service;
	}
}
