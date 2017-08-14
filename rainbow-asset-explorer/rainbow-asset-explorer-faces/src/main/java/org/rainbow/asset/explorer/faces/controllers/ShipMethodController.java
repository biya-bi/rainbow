/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.core.entities.ShipMethod;
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
public class ShipMethodController extends TrackableController<ShipMethod, Long, SearchOptions> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1953920499335759723L;
	
	@Autowired
	@Qualifier("shipMethodService")
	private Service<ShipMethod, Long, SearchOptions> service;
	
    public ShipMethodController() {
        super(ShipMethod.class);
    }

	@Override
	protected Service<ShipMethod, Long, SearchOptions> getService() {
		return service;
	}

}
