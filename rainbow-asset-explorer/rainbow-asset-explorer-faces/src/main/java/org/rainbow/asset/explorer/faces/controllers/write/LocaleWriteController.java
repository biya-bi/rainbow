package org.rainbow.asset.explorer.faces.controllers.write;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.service.services.LocaleService;
import org.rainbow.faces.controllers.write.AbstractWriteController;
import org.rainbow.faces.util.CrudNotificationInfo;
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
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "LocaleCreated", updatedMessageKey = "LocaleUpdated", deletedMessageKey = "LocaleDeleted")
public class LocaleWriteController extends AbstractWriteController<Locale> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2974160030565965598L;

	@Autowired
	private LocaleService service;

	public LocaleWriteController() {
		super(Locale.class);
	}

	@Override
	public Service<Locale> getService() {
		return service;
	}

}
