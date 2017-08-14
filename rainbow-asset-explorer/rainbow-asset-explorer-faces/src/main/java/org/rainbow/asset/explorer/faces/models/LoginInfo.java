/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.models;

import java.io.Serializable;

/**
 *
 * @author Biya-Bi
 */
public class LoginInfo implements Serializable {

	private static final long serialVersionUID = 1094801825228386363L;

	private String userName;
	private String password;
	private String rememberMe;

	public LoginInfo() {
	}

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

	/**
	 * Gets the remember me.
	 *
	 * @return the remember me
	 */
	public String getRememberMe() {
		return rememberMe;
	}

	/**
	 * Sets the remember me.
	 *
	 * @param rememberMe
	 *            the new remember me
	 */
	public void setRememberMe(String rememberMe) {
		this.rememberMe = rememberMe;
	}

}
