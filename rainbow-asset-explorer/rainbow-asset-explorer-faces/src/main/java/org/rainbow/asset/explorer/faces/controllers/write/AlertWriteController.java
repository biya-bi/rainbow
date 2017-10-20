package org.rainbow.asset.explorer.faces.controllers.write;

import static org.rainbow.asset.explorer.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.Alert;
import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.orm.entities.Schedule;
import org.rainbow.asset.explorer.service.exceptions.DuplicateAlertException;
import org.rainbow.asset.explorer.service.services.AlertService;
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
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "AlertCreated", updatedMessageKey = "AlertUpdated", deletedMessageKey = "AlertDeleted")
public class AlertWriteController extends AbstractWriteController<Alert> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3504002955882147749L;

	private List<EmailRecipient> selectedEmailRecipients;
	private List<EmailTemplate> selectedEmailTemplates;

	private static final String DUPLICATE_ALERT_ERROR_KEY = "DuplicateAlert";

	private final EnumTranslator translator;

	@Autowired
	private AlertService service;

	public AlertWriteController() {
		super(Alert.class);

		translator = new EnumTranslator();
	}

	@Override
	public Alert prepareCreate() {
		Alert alert = super.prepareCreate();
		alert.setEnabled(true);
		alert.setImmediate(true);

		Schedule schedule = new Schedule();
		schedule.setHour((byte) 0);
		schedule.setMinute((byte) 0);
		schedule.setSecond((byte) 0);
		schedule.setDayOfMonth("*");
		schedule.setTimezone("GMT");
		schedule.setYear("*");

		alert.setSchedule(schedule);
		return alert;
	}

	public List<EmailRecipient> getSelectedEmailRecipients() {
		return selectedEmailRecipients;
	}

	public void setSelectedEmailRecipients(List<EmailRecipient> selectedEmailRecipients) {
		this.selectedEmailRecipients = selectedEmailRecipients;
	}

	public void addEmailRecipients() throws Exception {
		if (this.getModel() != null && this.selectedEmailRecipients != null) {
			List<EmailRecipient> emailRecipients = this.getModel().getEmailRecipients();
			if (emailRecipients == null) {
				emailRecipients = new ArrayList<>();
				this.getModel().setEmailRecipients(emailRecipients);
			}
			for (EmailRecipient emailRecipient : selectedEmailRecipients) {
				if (!emailRecipients.contains(emailRecipient)) {
					emailRecipients.add(emailRecipient);
				}
			}
			this.edit();
		}
	}

	public void removeEmailRecipients() throws Exception {
		if (this.getModel() != null && this.selectedEmailRecipients != null) {
			List<EmailRecipient> emailRecipients = this.getModel().getEmailRecipients();
			if (emailRecipients == null) {
				emailRecipients = new ArrayList<>();
				this.getModel().setEmailRecipients(emailRecipients);
			}
			for (EmailRecipient emailRecipient : selectedEmailRecipients) {
				if (emailRecipients.contains(emailRecipient)) {
					emailRecipients.remove(emailRecipient);
				}
			}
			this.edit();
		}
	}

	public List<EmailTemplate> getSelectedEmailTemplates() {
		return selectedEmailTemplates;
	}

	public void setSelectedEmailTemplates(List<EmailTemplate> selectedEmailTemplates) {
		this.selectedEmailTemplates = selectedEmailTemplates;
	}

	public void addEmailTemplates() throws Exception {
		if (this.getModel() != null && this.selectedEmailTemplates != null) {
			List<EmailTemplate> emailTemplates = this.getModel().getEmailTemplates();
			if (emailTemplates == null) {
				emailTemplates = new ArrayList<>();
				this.getModel().setEmailTemplates(emailTemplates);
			}
			for (EmailTemplate emailTemplate : selectedEmailTemplates) {
				if (!emailTemplates.contains(emailTemplate)) {
					emailTemplates.add(emailTemplate);
				}
			}
			this.edit();
		}
	}

	public void removeEmailTemplates() throws Exception {
		if (this.getModel() != null && this.selectedEmailTemplates != null) {
			List<EmailTemplate> emailTemplates = this.getModel().getEmailTemplates();
			if (emailTemplates == null) {
				emailTemplates = new ArrayList<>();
				this.getModel().setEmailTemplates(emailTemplates);
			}
			for (EmailTemplate emailTemplate : selectedEmailTemplates) {
				if (emailTemplates.contains(emailTemplate)) {
					emailTemplates.remove(emailTemplate);
				}
			}
			this.edit();
		}
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateAlertException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateAlertException e = (DuplicateAlertException) throwable;
			FacesContextUtil.addErrorMessage(
					String.format(ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_ALERT_ERROR_KEY),
							translator.translate(e.getAlertType()), translator.translate(e.getAlertCategory())));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	public Service<Alert> getService() {
		return service;
	}
}
