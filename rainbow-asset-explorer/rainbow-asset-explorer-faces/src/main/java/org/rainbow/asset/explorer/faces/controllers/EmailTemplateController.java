package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.service.exceptions.DuplicateEmailTemplateNameException;
import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.services.Service;
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
@CrudNotificationInfo(createdMessageKey = "EmailTemplateCreated", updatedMessageKey = "EmailTemplateUpdated", deletedMessageKey = "EmailTemplateDeleted")
public class EmailTemplateController extends AuditableController<EmailTemplate, Integer, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1148813241965282527L;

	private static final String DUPLICATE_EMAIL_TEMPLATE_NAME_ERROR_KEY = "DuplicateEmailTemplateName";

	@Autowired
	@Qualifier("emailTemplateService")
	private Service<EmailTemplate, Integer, SearchOptions> service;

	public EmailTemplateController() {
		super(EmailTemplate.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateEmailTemplateNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateEmailTemplateNameException e = (DuplicateEmailTemplateNameException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_EMAIL_TEMPLATE_NAME_ERROR_KEY),
					e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected Service<EmailTemplate, Integer, SearchOptions> getService() {
		return service;
	}
}
