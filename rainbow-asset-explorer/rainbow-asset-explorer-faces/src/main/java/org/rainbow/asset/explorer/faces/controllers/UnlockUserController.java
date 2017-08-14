/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.models.SessionBean;
import org.rainbow.asset.explorer.faces.models.UnlockUserInfo;
import org.rainbow.asset.explorer.faces.utilities.JsfUtil;
import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;
import org.rainbow.security.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@RequestScoped
public class UnlockUserController implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7695720317194661162L;

	private final UnlockUserInfo unlockUserInfo = new UnlockUserInfo();

	private static final String USER_UNLOCK_SUCCESS_KEY = "UserUnlockedSuccessfully";
	private static final String USER_DOES_NOT_EXIST_ERROR_KEY = "UserDoesNotExist";
	private static final String USER_UNLOCK_FAILED_ERROR_KEY = "UserUnlockFailed";

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	public UnlockUserInfo getResetPasswordInfo() {
		if (SessionBean.isAuthenticated()) {
			unlockUserInfo.setUserName(SessionBean.getAuthentication().getName());
		}
		return unlockUserInfo;
	}

	public String checkUser() {
		if (!userService.userExists(unlockUserInfo.getUserName())) {
			ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
			JsfUtil.addErrorMessage(
					String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), unlockUserInfo.getUserName()),
					bundle.getString(USER_UNLOCK_FAILED_ERROR_KEY));
		} else {
			try {
				unlockUserInfo.setQuestion(userService.getSecurityQuestion(unlockUserInfo.getUserName()));
				return "success";
			} catch (UsernameNotFoundException e) {
				Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
				ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
				JsfUtil.addErrorMessage(
						String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), unlockUserInfo.getUserName()),
						bundle.getString(USER_UNLOCK_FAILED_ERROR_KEY));
			}
		}
		return "failure";
	}

	public String unlockUser() {
		try {
			userService.unlock(unlockUserInfo.getUserName(), unlockUserInfo.getQuestion(), unlockUserInfo.getAnswer());
			JsfUtil.addSuccessMessage(
					ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES).getString(USER_UNLOCK_SUCCESS_KEY));
			return "success";
		} catch (UsernameNotFoundException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
			ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
			JsfUtil.addErrorMessage(
					String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), unlockUserInfo.getUserName()),
					bundle.getString(USER_UNLOCK_FAILED_ERROR_KEY));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							authenticationExceptionHandler.handle(this.getClass(), e), ResourceBundle
									.getBundle(SECURITY_MESSAGES).getString(USER_UNLOCK_FAILED_ERROR_KEY)));
		}
		return "failure";
	}

}
