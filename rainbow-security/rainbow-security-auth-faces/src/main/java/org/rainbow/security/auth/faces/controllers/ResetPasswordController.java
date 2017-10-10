package org.rainbow.security.auth.faces.controllers;

import static org.rainbow.security.auth.faces.utilities.ResourceBundles.MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.security.auth.faces.utilities.ResourceBundles;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@Named
@SessionScope
public class ResetPasswordController extends AbstractQuestionAnswerController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8944275670517937457L;
	private static final String PASSWORD_RESET_SUCCESS_KEY = "PasswordResetSuccessful";
	private static final String USER_DOES_NOT_EXIST_ERROR_KEY = "UserDoesNotExist";
	private static final String PASSWORD_RESET_FAILED_ERROR_KEY = "PasswordResetFailed";

	@Autowired
	@Qualifier("userAccountService")
	private UserAccountService userAccountService;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	private String newPassword;

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String resetPassword() {
		try {
			userAccountService.resetPassword(this.getUserName(), this.getNewPassword(), this.getQuestion(),
					this.getAnswer());
			FacesContextUtil.addSuccessMessage(
					ResourceBundle.getBundle(ResourceBundles.MESSAGES).getString(PASSWORD_RESET_SUCCESS_KEY));
			return SUCCESS;
		} catch (UsernameNotFoundException | UserNotFoundException e) {
			logger.log(Level.SEVERE, null, e);
			ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.MESSAGES);
			FacesContextUtil.addErrorMessage(
					String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), this.getUserName()),
					bundle.getString(PASSWORD_RESET_FAILED_ERROR_KEY));
		} catch (Exception e) {
			FacesContextUtil.addMessage(FacesMessage.SEVERITY_WARN,
					authenticationExceptionHandler.handle(this.getClass(), e),
					ResourceBundle.getBundle(MESSAGES).getString(PASSWORD_RESET_FAILED_ERROR_KEY));
		}

		return FAILURE;
	}

	@Override
	protected UserAccountService getUserAccountService() {
		return userAccountService;
	}

	@Override
	protected String getErrorDetailKey() {
		return PASSWORD_RESET_FAILED_ERROR_KEY;
	}
}
