package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.models.ChangePasswordInfo;
import org.rainbow.asset.explorer.faces.models.SessionBean;
import org.rainbow.security.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Named
@RequestScoped
public class ChangePasswordController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3855917317198389339L;

	private final ChangePasswordInfo changePasswordInfo = new ChangePasswordInfo();

	private static final String PASSWORD_CHANGE_FAILED_KEY = "PasswordChangeFailed";

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	public String changePassword() {
		try {
			userService.changePassword(changePasswordInfo.getOldPassword(), changePasswordInfo.getNewPassword());
			return "success";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							authenticationExceptionHandler.handle(this.getClass(), e),
							ResourceBundle.getBundle(SECURITY_MESSAGES).getString(PASSWORD_CHANGE_FAILED_KEY)));
		}
		return "failure";
	}

	public ChangePasswordInfo getChangePasswordInfo() {
		if (SessionBean.isAuthenticated()) {
			changePasswordInfo.setUserName(SessionBean.getAuthentication().getName());
		}
		return changePasswordInfo;
	}

}
