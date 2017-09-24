package org.rainbow.security.auth.faces.controllers;

import org.rainbow.security.auth.faces.utilities.SessionUtil;

public abstract class AbstractUserNameController extends AbstractOutcomeController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 267111474877356196L;

	private String userName;

	public String getUserName() {
		if (userName == null || userName.isEmpty()) {
			userName = SessionUtil.getUserName();
		}
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
