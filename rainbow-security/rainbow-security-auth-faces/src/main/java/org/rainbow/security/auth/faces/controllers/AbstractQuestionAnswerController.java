package org.rainbow.security.auth.faces.controllers;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;

import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.security.auth.faces.utilities.ResourceBundles;
import org.rainbow.security.service.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public abstract class AbstractQuestionAnswerController extends AbstractUserNameController {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2176242370337757285L;

	protected static final String USER_DOES_NOT_EXIST_ERROR_KEY = "UserDoesNotExist";

	protected final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	private String question;
	private String answer;

	public String getQuestion() {
		return question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	protected abstract UserAccountService getUserAccountService();

	protected abstract String getErrorDetailKey();

	public String prepare() {
		checkDependencies();
		return getRecoveryQuestion(this.getUserName(), this.getErrorDetailKey());
	}

	private String getRecoveryQuestion(String userName, String errorDetailKey) {
		final ResourceBundle bundle = ResourceBundle.getBundle(ResourceBundles.MESSAGES);
		if (!this.getUserAccountService().userExists(userName)) {

			final String message = String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), userName);

			logger.warning(message);

			FacesContextUtil.addErrorMessage(message, bundle.getString(errorDetailKey));
		} else {
			try {
				this.question = this.getUserAccountService().getRecoveryQuestion(userName);
				return SUCCESS;
			} catch (UsernameNotFoundException e) {
				logger.log(Level.WARNING, null, e);

				final String message = String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), userName);

				FacesContextUtil.addErrorMessage(message, bundle.getString(errorDetailKey));
			} catch (Exception e) {
				FacesContextUtil.addMessage(FacesMessage.SEVERITY_WARN,
						authenticationExceptionHandler.handle(this.getClass(), e), bundle.getString(errorDetailKey));
			}
		}
		return FAILURE;
	}

	protected void checkDependencies() {
		if (this.getUserAccountService() == null) {
			throw new IllegalStateException("The user account service cannot be null.");
		}
		if (this.getErrorDetailKey() == null) {
			throw new IllegalStateException("The error detail key cannot be null.");
		}
	}
}
