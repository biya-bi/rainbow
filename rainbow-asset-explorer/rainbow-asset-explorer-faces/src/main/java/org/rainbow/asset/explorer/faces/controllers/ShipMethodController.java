package org.rainbow.asset.explorer.faces.controllers;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.asset.explorer.service.services.ShipMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class ShipMethodController extends AuditableController<ShipMethod> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1953920499335759723L;
	
	@Autowired
	private ShipMethodService service;
	
    public ShipMethodController() {
        super(ShipMethod.class);
    }

	@Override
	protected ShipMethodService getService() {
		return service;
	}

}
