package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.security.faces.util.CrudNotificationInfo;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.service.exceptions.DuplicateAuthorityException;
import org.rainbow.security.service.services.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "AuthorityCreated", updatedMessageKey = "AuthorityUpdated", deletedMessageKey = "AuthorityDeleted")
public class AuthorityController extends AuditableController<Authority> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1613718845482244375L;

	private static final String DUPLICATE_AUTHORITY_NAME_ERROR_KEY = "DuplicateAuthorityName";

	@Autowired
	private AuthorityService authorityService;

	public AuthorityController() {
		super(Authority.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateAuthorityException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateAuthorityException e = (DuplicateAuthorityException) throwable;
			FacesContextUtil.addErrorMessage(
					String.format(ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_AUTHORITY_NAME_ERROR_KEY),
							e.getAuthorityName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected AuthorityService getService() {
		return authorityService;
	}
}
