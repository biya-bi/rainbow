package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.security.faces.utilities.CrudNotificationInfo;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.service.exceptions.DuplicateAuthorityException;
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
@CrudNotificationInfo(createdMessageKey = "AuthorityCreated", updatedMessageKey = "AuthorityUpdated", deletedMessageKey = "AuthorityDeleted")
public class AuthorityController extends AuditableController<Authority, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1613718845482244375L;

	private static final String DUPLICATE_AUTHORITY_NAME_ERROR_KEY = "DuplicateAuthorityName";

	@Autowired
	@Qualifier("authorityService")
	private Service<Authority, Long, SearchOptions> authorityService;

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
	protected Service<Authority, Long, SearchOptions> getService() {
		return authorityService;
	}
}
