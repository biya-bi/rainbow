/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class WrongPasswordQuestionAnswerException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -16909515899844654L;
	private final String userName;
    private final String applicationName;

    public WrongPasswordQuestionAnswerException(String userName, String applicationName) {
        this(userName, applicationName, String.format("A wrong password question anwser has been provided for the user '%s' of the application '%s'.", userName, applicationName));
    }

    public WrongPasswordQuestionAnswerException(String userName, String applicationName, String message) {
        super(message);
        this.userName = userName;
        this.applicationName = applicationName;
    }

    public String getUserName() {
        return userName;
    }

    public String getApplicationName() {
        return applicationName;
    }
}
