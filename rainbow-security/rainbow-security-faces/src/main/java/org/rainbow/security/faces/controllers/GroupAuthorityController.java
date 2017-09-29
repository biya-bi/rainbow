package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
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
public class GroupAuthorityController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1845363878303672666L;

	private static final String AUTHORITIES_GRANTED_TO_GROUPS_KEY = "AuthoritiesGrantedToGroupsSuccessfully";
	private static final String AUTHORITIES_REVOKED_FROM_GROUPS_KEY = "AuthoritiesRevokedFromGroupsSuccessfully";

	private List<Group> groups;
	private List<Authority> authorities;
	private Group group;
	private Authority authority;

	@Autowired
	@Qualifier("groupService")
	private Service<Group, Long, SearchOptions> groupService;

	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	public List<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}

	private String getMessage(String baseName, String key) {
		return ResourceBundle.getBundle(baseName).getString(key);
	}

	public void grant() throws Exception {
		List<Group> groupsToBeModified = new ArrayList<>();
		for (Group group : this.getGroups()) {
			List<Authority> groupAuthorities = group.getAuthorities();
			if (groupAuthorities == null) {
				groupAuthorities = new ArrayList<>();
				group.setAuthorities(groupAuthorities);
			}
			for (Authority authority : this.getAuthorities()) {
				if (!groupAuthorities.contains(authority)) {
					groupAuthorities.add(authority);
					if (!groupsToBeModified.contains(group)) {
						groupsToBeModified.add(group);
					}
				}
			}
		}
		if (!groupsToBeModified.isEmpty()) {
			groupService.update(groupsToBeModified);
			FacesContextUtil.addSuccessMessage(getMessage(CRUD_MESSAGES, AUTHORITIES_GRANTED_TO_GROUPS_KEY));
		}
	}

	public void revoke() throws Exception {
		List<Group> groupsToBeModified = new ArrayList<>();
		for (Group group : this.getGroups()) {
			List<Authority> groupAuthorities = group.getAuthorities();
			if (groupAuthorities == null) {
				groupAuthorities = new ArrayList<>();
				group.setAuthorities(groupAuthorities);
			}
			for (Authority user : this.getAuthorities()) {
				if (groupAuthorities.contains(user)) {
					groupAuthorities.remove(user);
					if (!groupsToBeModified.contains(group)) {
						groupsToBeModified.add(group);
					}
				}
			}
		}
		if (!groupsToBeModified.isEmpty()) {
			groupService.update(groupsToBeModified);
			FacesContextUtil.addSuccessMessage(getMessage(CRUD_MESSAGES, AUTHORITIES_REVOKED_FROM_GROUPS_KEY));
		}
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

	public Authority getAuthority() {
		return authority;
	}

	public void setAuthority(Authority authority) {
		if (authority != null) {
			List<Authority> authorityList = new ArrayList<>();
			authorityList.add(authority);
			authorities = authorityList;
		}
		this.authority = authority;
	}

	private boolean listsAreNotEmpty() {
		return this.getAuthorities() != null && !this.getAuthorities().isEmpty() && this.getGroups() != null
				&& !this.getGroups().isEmpty();
	}

	public boolean canGrant() {
		if (!listsAreNotEmpty()) {
			return false;
		}
		for (Group group : this.getGroups()) {
			List<Authority> groupAuthorities = group.getAuthorities();
			if (groupAuthorities == null) {
				return true;
			}
			for (Authority authority : this.getAuthorities()) {
				if (!groupAuthorities.contains(authority)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean canRevoke() {
		if (!listsAreNotEmpty()) {
			return false;
		}
		for (Group group : this.getGroups()) {
			List<Authority> groupAuthorities = group.getAuthorities();
			if (groupAuthorities != null) {
				for (Authority authority : this.getAuthorities()) {
					if (groupAuthorities.contains(authority)) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
