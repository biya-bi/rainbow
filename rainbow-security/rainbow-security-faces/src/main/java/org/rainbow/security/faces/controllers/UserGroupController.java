package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.User;
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
public class UserGroupController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2240966119915578089L;

	private static final String USERS_ADDED_TO_GROUPS_KEY = "UsersAddedToGroupsSuccessfully";
	private static final String USERS_REMOVED_FROM_GROUPS_KEY = "UsersRemovedFromGroupsSuccessfully";

	private List<Group> groups;
	private List<User> users;
	private User user;
	private Group group;

	@Autowired
	private GroupService groupService;

	public UserGroupController() {
	}

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		if (user != null) {
			List<User> userList = new ArrayList<>();
			userList.add(user);
			users = userList;
		}
		this.user = user;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		if (group != null) {
			List<Group> groupList = new ArrayList<>();
			groupList.add(group);
			groups = groupList;
		}
		this.group = group;
	}

	private String getMessage(String baseName, String key) {
		return ResourceBundle.getBundle(baseName).getString(key);
	}

	public void add() throws Exception {
		List<Group> groupsToBeModified = new ArrayList<>();
		for (Group group : this.getGroups()) {
			List<User> groupUsers = group.getUsers();
			if (groupUsers == null) {
				groupUsers = new ArrayList<>();
				group.setUsers(groupUsers);
			}
			for (User user : this.getUsers()) {
				if (!groupUsers.contains(user)) {
					groupUsers.add(user);
					if (!groupsToBeModified.contains(group)) {
						groupsToBeModified.add(group);
					}
				}
			}
		}
		if (!groupsToBeModified.isEmpty()) {
			groupService.update(groupsToBeModified);
			FacesContextUtil.addSuccessMessage(getMessage(CRUD_MESSAGES, USERS_ADDED_TO_GROUPS_KEY));
		}
	}

	public void remove() throws Exception {
		List<Group> groupsToBeModified = new ArrayList<>();
		for (Group group : this.getGroups()) {
			List<User> groupUsers = group.getUsers();
			if (groupUsers == null) {
				groupUsers = new ArrayList<>();
				group.setUsers(groupUsers);
			}
			for (User user : this.getUsers()) {
				if (groupUsers.contains(user)) {
					groupUsers.remove(user);
					if (!groupsToBeModified.contains(group)) {
						groupsToBeModified.add(group);
					}
				}
			}
		}
		if (!groupsToBeModified.isEmpty()) {
			groupService.update(groupsToBeModified);
			FacesContextUtil.addSuccessMessage(getMessage(CRUD_MESSAGES, USERS_REMOVED_FROM_GROUPS_KEY));
		}
	}

	private boolean listsAreNotEmpty() {
		return this.getUsers() != null && !this.getUsers().isEmpty() && this.getGroups() != null
				&& !this.getGroups().isEmpty();
	}

	public boolean canAdd() {
		if (!listsAreNotEmpty()) {
			return false;
		}
		for (Group group : this.getGroups()) {
			List<User> groupUsers = group.getUsers();
			if (groupUsers == null) {
				return true;
			}
			for (User user : this.getUsers()) {
				if (!groupUsers.contains(user)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean canRemove() {
		if (!listsAreNotEmpty()) {
			return false;
		}
		for (Group group : this.getGroups()) {
			List<User> groupUsers = group.getUsers();
			if (groupUsers != null) {
				for (User user : this.getUsers()) {
					if (groupUsers.contains(user)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
