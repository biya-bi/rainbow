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

import org.rainbow.asset.explorer.core.entities.EmailRecipient;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateEmailRecipientEmailException;
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
@CrudNotificationInfo(createdMessageKey = "EmailRecipientCreated", updatedMessageKey = "EmailRecipientUpdated", deletedMessageKey = "EmailRecipientDeleted")
public class EmailRecipientController extends TrackableController<EmailRecipient, Integer, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2041818766114274904L;

	private static final String DUPLICATE_EMAIL_RECIPIENT_EMAIL_ERROR_KEY = "DuplicateEmailRecipientEmail";

	@Autowired
	@Qualifier("emailRecipientService")
	private Service<EmailRecipient, Integer, SearchOptions> service;

	public EmailRecipientController() {
		super(EmailRecipient.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateEmailRecipientEmailException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateEmailRecipientEmailException e = (DuplicateEmailRecipientEmailException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_EMAIL_RECIPIENT_EMAIL_ERROR_KEY),
					e.getEmail()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected Service<EmailRecipient, Integer, SearchOptions> getService() {
		return service;
	}
}
