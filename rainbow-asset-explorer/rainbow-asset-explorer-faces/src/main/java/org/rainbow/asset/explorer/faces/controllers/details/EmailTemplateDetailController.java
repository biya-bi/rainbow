package org.rainbow.asset.explorer.faces.controllers.details;

import static org.rainbow.asset.explorer.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.service.exceptions.DuplicateEmailTemplateNameException;
import org.rainbow.asset.explorer.service.services.EmailTemplateService;
import org.rainbow.faces.controllers.details.AbstractAuditableEntityDetailController;
import org.rainbow.faces.util.CrudNotificationInfo;
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
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "EmailTemplateCreated", updatedMessageKey = "EmailTemplateUpdated", deletedMessageKey = "EmailTemplateDeleted")
public class EmailTemplateDetailController extends AbstractAuditableEntityDetailController<EmailTemplate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1148813241965282527L;

	private static final String DUPLICATE_EMAIL_TEMPLATE_NAME_ERROR_KEY = "DuplicateEmailTemplateName";

	@Autowired
	private EmailTemplateService service;

	public EmailTemplateDetailController() {
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
	protected EmailTemplateService getService() {
		return service;
	}
}
