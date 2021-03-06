package org.rainbow.asset.explorer.faces.controllers.write;

import static org.rainbow.asset.explorer.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.service.exceptions.DuplicateEmailRecipientEmailException;
import org.rainbow.asset.explorer.service.services.EmailRecipientService;
import org.rainbow.faces.controllers.write.AbstractWriteController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.faces.util.FacesContextUtil;
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
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "EmailRecipientCreated", updatedMessageKey = "EmailRecipientUpdated", deletedMessageKey = "EmailRecipientDeleted")
public class EmailRecipientWriteController extends AbstractWriteController<EmailRecipient> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2041818766114274904L;

	private static final String DUPLICATE_EMAIL_RECIPIENT_EMAIL_ERROR_KEY = "DuplicateEmailRecipientEmail";

	@Autowired
	private EmailRecipientService service;

	public EmailRecipientWriteController() {
		super(EmailRecipient.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateEmailRecipientEmailException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateEmailRecipientEmailException e = (DuplicateEmailRecipientEmailException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_EMAIL_RECIPIENT_EMAIL_ERROR_KEY),
					e.getEmail()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	public Service<EmailRecipient> getService() {
		return service;
	}
}
