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

import org.rainbow.asset.explorer.faces.models.ResetPasswordInfo;
import org.rainbow.asset.explorer.faces.models.SessionBean;
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
public class ResetPasswordController implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5210493504972283008L;

	@Autowired
	private UserService userService;

	private final ResetPasswordInfo resetPasswordInfo = new ResetPasswordInfo();

	private static final String PASSWORD_RESET_SUCCESS_KEY = "PasswordResetSuccessfully";
	private static final String USER_DOES_NOT_EXIST_ERROR_KEY = "UserDoesNotExist";
	private static final String PASSWORD_RESET_FAILED_ERROR_KEY = "PasswordResetFailed";

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	public ResetPasswordInfo getResetPasswordInfo() {
		if (SessionBean.isAuthenticated()) {
			resetPasswordInfo.setUserName(SessionBean.getAuthentication().getName());
		}
		return resetPasswordInfo;
	}

	public String checkUser() {
		if (!userService.userExists(resetPasswordInfo.getUserName())) {
			final ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);

			final String message = String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY),
					resetPasswordInfo.getUserName());

			logger.warning(message);

			JsfUtil.addErrorMessage(message, bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
		} else {
			try {
				resetPasswordInfo.setQuestion(userService.getSecurityQuestion(resetPasswordInfo.getUserName()));
				return "success";
			} catch (UsernameNotFoundException e) {

				logger.log(Level.WARNING, null, e);

				final ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);

				final String message = String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY),
						resetPasswordInfo.getUserName());

				JsfUtil.addErrorMessage(message, bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
			} catch (Exception e) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						authenticationExceptionHandler.handle(this.getClass(), e),
						ResourceBundle.getBundle(SECURITY_MESSAGES).getString(PASSWORD_RESET_FAILED_ERROR_KEY)));
			}
		}
		return "failure";
	}

	// public String checkAnswer() {
	// try {
	// SecurityProvider provider = getService().getSecurityProvider();
	// String userName = resetPasswordInfo.getUserName();
	// if (provider.isUserLocked(userName,
	// applicationConfiguration.getApplicationName())) {
	// ResourceBundle bundle =
	// ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
	// JsfUtil.addErrorMessage(String.format(bundle.getString(USER_LOCKED_OUT_ERROR_KEY),
	// userName),
	// bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
	// return null;
	// }
	// if (!provider.isUserApproved(userName,
	// applicationConfiguration.getApplicationName())) {
	// ResourceBundle bundle =
	// ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
	// JsfUtil.addErrorMessage(String.format(bundle.getString(USER_NOT_APPROVED_ERROR_KEY),
	// userName),
	// bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
	// return null;
	// }
	// provider.validatePasswordQuestionAnswer(resetPasswordInfo.getUserName(),
	// resetPasswordInfo.getPasswordQuestionAnswer(),
	// applicationConfiguration.getApplicationName());
	// return "/security/resetPassword/supplyNewPassword";
	// } catch (UserNotFoundException_Exception ex) {
	// Logger.getLogger(ResetPasswordController.class.getName()).log(Level.SEVERE,
	// null, ex);
	// ResourceBundle bundle =
	// ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
	// JsfUtil.addErrorMessage(
	// String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY),
	// resetPasswordInfo.getUserName()),
	// bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
	// } catch (WrongPasswordQuestionAnswerException_Exception ex) {
	// ResourceBundle bundle =
	// ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
	// JsfUtil.addErrorMessage(bundle.getString(WRONG_PASSWORD_QUESTION_ANSWER_ERROR_KEY),
	// bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
	// }
	// return null;
	// }

	public String resetPassword() {
		try {
			userService.resetPassword(resetPasswordInfo.getUserName(), resetPasswordInfo.getNewPassword(),
					resetPasswordInfo.getQuestion(), resetPasswordInfo.getAnswer());
			JsfUtil.addSuccessMessage(
					ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES).getString(PASSWORD_RESET_SUCCESS_KEY));
			return "success";
		} catch (UsernameNotFoundException e) {
			Logger.getLogger(ResetPasswordController.class.getName()).log(Level.SEVERE, null, e);
			ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.SECURITY_MESSAGES);
			JsfUtil.addErrorMessage(
					String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), resetPasswordInfo.getUserName()),
					bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							authenticationExceptionHandler.handle(this.getClass(), e), ResourceBundle
									.getBundle(SECURITY_MESSAGES).getString(PASSWORD_RESET_FAILED_ERROR_KEY)));
		}

		return "failure";
	}

}
