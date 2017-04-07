/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.core.persistence.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateProfileUserNameException extends RainbowJournalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1954274083946222025L;
	private final String userName;

	public DuplicateProfileUserNameException(String userName) {
		this(userName, String.format("A profile with user userName '%s' already exists.", userName));
	}

	public DuplicateProfileUserNameException(String userName, String message) {
		super(message);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
}
