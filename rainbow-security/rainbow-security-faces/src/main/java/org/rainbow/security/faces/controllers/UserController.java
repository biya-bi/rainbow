package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.utilities.ResourceBundles.CRUD_MESSAGES;
import static org.rainbow.security.faces.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.security.faces.utilities.CrudNotificationInfo;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.DuplicateUserException;
import org.rainbow.security.service.exceptions.InvalidPasswordException;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "UserCreated", updatedMessageKey = "UserUpdated", deletedMessageKey = "UserDeleted")
public class UserController extends AuditableController<User, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4687014310953094286L;

	private static final String DUPLICATE_USER_NAME_ERROR_KEY = "DuplicateUserName";
	private static final String INVALID_PASSWORD_KEY = "InvalidPassword";

	@Autowired
	@Qualifier("userService")
	private Service<User, Long, SearchOptions> userService;

	public UserController() {
		super(User.class);
	}

	@Override
	public User prepareCreate() {
		User user = super.prepareCreate();
		Membership membership = new Membership();
		membership.setEnabled(true);
		user.setMembership(membership);
		membership.setUser(user);
		return user;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateUserException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateUserException e = (DuplicateUserException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_USER_NAME_ERROR_KEY), e.getUserName()));
			return true;
		}
		if (throwable instanceof InvalidPasswordException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			FacesContextUtil
					.addErrorMessage(ResourceBundle.getBundle(SECURITY_MESSAGES).getString(INVALID_PASSWORD_KEY));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected Service<User, Long, SearchOptions> getService() {
		return userService;
	}
}
