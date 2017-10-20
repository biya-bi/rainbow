package org.rainbow.security.faces.controllers.write;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.faces.controllers.write.AbstractWriteController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.security.faces.util.ResourceBundles;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.service.exceptions.DuplicateAuthorityException;
import org.rainbow.security.service.services.AuthorityService;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "AuthorityCreated", updatedMessageKey = "AuthorityUpdated", deletedMessageKey = "AuthorityDeleted")
public class AuthorityWriteController extends AbstractWriteController<Authority> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1613718845482244375L;

	private static final String DUPLICATE_AUTHORITY_NAME_ERROR_KEY = "DuplicateAuthorityName";

	@Autowired
	private AuthorityService authorityService;

	public AuthorityWriteController() {
		super(Authority.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateAuthorityException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateAuthorityException e = (DuplicateAuthorityException) throwable;
			FacesContextUtil.addErrorMessage(
					String.format(ResourceBundle.getBundle(ResourceBundles.CRUD_MESSAGES).getString(DUPLICATE_AUTHORITY_NAME_ERROR_KEY),
							e.getAuthorityName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	public Service<Authority> getService() {
		return authorityService;
	}
}
