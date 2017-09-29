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
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.exceptions.DuplicateGroupException;
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
@CrudNotificationInfo(createdMessageKey = "GroupCreated", updatedMessageKey = "GroupUpdated", deletedMessageKey = "GroupDeleted")
public class GroupController extends AuditableController<Group, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3529036700483972954L;

	private static final String DUPLICATE_GROUP_NAME_ERROR_KEY = "DuplicateGroupName";

	@Autowired
	@Qualifier("groupService")
	private Service<Group, Long, SearchOptions> groupService;

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
	protected Service<Group, Long, SearchOptions> getService() {
		return groupService;
	}

}
