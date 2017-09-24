package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocationNameException;
import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.Service;
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
@CrudNotificationInfo(createdMessageKey = "LocationCreated", updatedMessageKey = "LocationUpdated", deletedMessageKey = "LocationDeleted")
public class LocationController extends AuditableController<Location, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2981018316085136412L;

	private static final String DUPLICATE_LOCATION_NAME_ERROR_KEY = "DuplicateLocationName";

	@Autowired
	@Qualifier("locationService")
	private Service<Location, Long, SearchOptions> service;

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
	protected Service<Location, Long, SearchOptions> getService() {
		return service;
	}
}
