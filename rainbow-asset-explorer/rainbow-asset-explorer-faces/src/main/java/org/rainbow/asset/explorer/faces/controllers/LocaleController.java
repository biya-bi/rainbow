/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.core.entities.Locale;
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
public class LocaleController extends TrackableController<Locale, Integer, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2974160030565965598L;

	@Autowired
	@Qualifier("localeService")
	private Service<Locale, Integer, SearchOptions> service;

	public LocaleController() {
		super(Locale.class);
	}

	@Override
	protected Service<Locale, Integer, SearchOptions> getService() {
		return service;
	}

}
