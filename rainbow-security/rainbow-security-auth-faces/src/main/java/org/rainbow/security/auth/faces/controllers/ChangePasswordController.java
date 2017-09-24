package org.rainbow.security.auth.faces.controllers;

import static org.rainbow.security.auth.faces.utilities.ResourceBundles.MESSAGES;

import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.security.auth.faces.utilities.SessionUtil;
import org.rainbow.security.service.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@Named
@RequestScope
public class ChangePasswordController extends AbstractUserNameController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1738451578127921267L;

	private static final String PASSWORD_CHANGE_FAILED_KEY = "PasswordChangeFailed";

	@Autowired
	@Qualifier("userAccountService")
	private UserAccountService userAccountService;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	private String oldPassword;
	private String newPassword;

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public boolean isAuthenticated() {
		return SessionUtil.isAuthenticated();
	}

	public String changePassword() {
		try {
			userAccountService.changePassword(this.getUserName(), this.getOldPassword(), this.getNewPassword());
			return SUCCESS;
		} catch (Exception e) {
			FacesContextUtil.addMessage(FacesMessage.SEVERITY_WARN,
					authenticationExceptionHandler.handle(this.getClass(), e),
					ResourceBundle.getBundle(MESSAGES).getString(PASSWORD_CHANGE_FAILED_KEY));
		}
		return FAILURE;
	}

	@Override
	public String getUserName() {
		if (super.getUserName() == null || super.getUserName().isEmpty()) {
			final FacesContext currentInstance = FacesContext.getCurrentInstance();
			if (currentInstance != null && currentInstance.getExternalContext() != null) {
				final Map<String, String> params = currentInstance.getExternalContext().getRequestParameterMap();
				final String userNameKey = "user_name";
				if (params.containsKey(userNameKey)) {
					super.setUserName(params.get(userNameKey));
				}
			}
		}
		return super.getUserName();
	}
}
