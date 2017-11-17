package org.rainbow.journal.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateProfileException extends RainbowJournalException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1954274083946222025L;
	private final String userName;

	public DuplicateProfileException(String userName) {
		this(userName, String.format("A profile with user name '%s' already exists.", userName));
	}

	public DuplicateProfileException(String userName, String message) {
		super(message);
		this.userName = userName;
	}

	public String getUserName() {
		return userName;
	}
}
