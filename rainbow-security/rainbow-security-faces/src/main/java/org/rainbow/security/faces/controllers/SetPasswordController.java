package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.security.service.exceptions.UserNotFoundException;
import org.rainbow.security.service.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@Named
public class SetPasswordController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2304144597982056323L;
	private static final String USER_DOES_NOT_EXIST_ERROR_KEY = "UserDoesNotExist";
	private static final String PASSWORD_SET_FAILED_ERROR_KEY = "PasswordSetFailed";
	private static final String INVALID_PASSWORD_ERROR_KEY = "InvalidPassword";
	private static final String COMMITTED_FLAG = "committed";

	private final Logger logger = Logger.getLogger(this.getClass().getName());

	@Autowired
	@Qualifier("userAccountService")
	private UserAccountService userAccountService;

	private String userName;
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPassword() {
		try {
			userAccountService.setPassword(this.getUserName(), this.getPassword());
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
			return;
		} catch (InvalidPasswordException e) {
			ResourceBundle bundle = ResourceBundle.getBundle(SECURITY_MESSAGES);
			logger.log(Level.WARNING, null, e);
			FacesContextUtil.addErrorMessage(bundle.getString(INVALID_PASSWORD_ERROR_KEY));
		} catch (UsernameNotFoundException | UserNotFoundException e) {
			ResourceBundle bundle = ResourceBundle.getBundle(SECURITY_MESSAGES);
			logger.log(Level.SEVERE, null, e);
			FacesContextUtil.addErrorMessage(
					String.format(bundle.getString(USER_DOES_NOT_EXIST_ERROR_KEY), this.getUserName()),
					bundle.getString(PASSWORD_SET_FAILED_ERROR_KEY));
		}
		RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
	}

}
