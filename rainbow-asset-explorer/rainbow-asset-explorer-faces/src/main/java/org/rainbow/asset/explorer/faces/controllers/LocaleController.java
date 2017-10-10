package org.rainbow.asset.explorer.faces.controllers;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.service.services.LocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class LocaleController extends AuditableController<Locale> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2974160030565965598L;

	@Autowired
	private LocaleService service;

	public LocaleController() {
		super(Locale.class);
	}

	@Override
	protected LocaleService getService() {
		return service;
	}

}
