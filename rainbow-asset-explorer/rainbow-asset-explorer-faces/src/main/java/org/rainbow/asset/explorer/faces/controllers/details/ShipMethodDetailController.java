package org.rainbow.asset.explorer.faces.controllers.details;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.service.services.ShipMethodService;
import org.rainbow.faces.controllers.details.AbstractAuditableDetailController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "ShipMethodCreated", updatedMessageKey = "ShipMethodUpdated", deletedMessageKey = "ShipMethodDeleted")
public class ShipMethodDetailController extends AbstractAuditableDetailController<ShipMethod> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1953920499335759723L;

	@Autowired
	private ShipMethodService service;

	public ShipMethodDetailController() {
		super(ShipMethod.class);
	}

	@Override
	protected ShipMethodService getService() {
		return service;
	}

}
