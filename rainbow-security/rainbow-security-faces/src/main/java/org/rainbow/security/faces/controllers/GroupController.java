package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.security.faces.util.CrudNotificationInfo;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.exceptions.DuplicateGroupException;
import org.rainbow.security.service.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "GroupCreated", updatedMessageKey = "GroupUpdated", deletedMessageKey = "GroupDeleted")
public class GroupController extends AuditableController<Group> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3529036700483972954L;

	private static final String DUPLICATE_GROUP_NAME_ERROR_KEY = "DuplicateGroupName";

	@Autowired
	private GroupService groupService;

	public GroupController() {
		super(Group.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateGroupException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateGroupException e = (DuplicateGroupException) throwable;
			FacesContextUtil.addErrorMessage(
					String.format(ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_GROUP_NAME_ERROR_KEY),
							e.getGroupName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected GroupService getService() {
		return groupService;
	}

}
