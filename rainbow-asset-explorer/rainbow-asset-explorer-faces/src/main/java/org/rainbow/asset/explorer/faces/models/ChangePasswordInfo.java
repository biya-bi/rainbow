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
public class ChangePasswordInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 168027912571970556L;
	private String userName;
    private String oldPassword;
    private String newPassword;

    public ChangePasswordInfo() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
