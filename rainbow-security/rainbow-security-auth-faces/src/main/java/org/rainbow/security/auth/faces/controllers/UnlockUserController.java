package org.rainbow.security.auth.faces.controllers;

import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.faces.application.FacesMessage;
import javax.inject.Named;

import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.security.auth.faces.utilities.ResourceBundles;
import org.rainbow.security.service.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@Named
@SessionScope
public class UnlockUserController extends AbstractQuestionAnswerController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9192156281171092125L;
	private static final String USER_UNLOCK_SUCCESS_KEY = "UserUnlockSuccessful";
	private static final String USER_UNLOCK_FAILED_ERROR_KEY = "UserUnlockFailed";

	@Autowired
	@Qualifier("userAccountService")
	private UserAccountService userAccountService;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	public String unlock() {
		checkDependencies();
		final ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.MESSAGES);
		try {
			userAccountService.unlock(this.getUserName(), this.getQuestion(), this.getAnswer());
			FacesContextUtil.addSuccessMessage(bundle.getString(USER_UNLOCK_SUCCESS_KEY));
			return SUCCESS;
		} catch (UsernameNotFoundException e) {
			logger.log(Level.SEVERE, null, e);
			FacesContextUtil.addErrorMessage(
					String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), this.getUserName()),
					bundle.getString(USER_UNLOCK_FAILED_ERROR_KEY));
		} catch (Exception e) {
			FacesContextUtil.addMessage(FacesMessage.SEVERITY_WARN,
					authenticationExceptionHandler.handle(this.getClass(), e),
					bundle.getString(USER_UNLOCK_FAILED_ERROR_KEY));
		}
		return FAILURE;
	}

	@Override
	protected UserAccountService getUserAccountService() {
		return userAccountService;
	}

	@Override
	protected String getErrorDetailKey() {
		return USER_UNLOCK_FAILED_ERROR_KEY;
	}
}
