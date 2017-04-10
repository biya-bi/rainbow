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
public class DuplicateSubscriptionException extends RainbowJournalException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5809793048063548534L;
	private final String subscriberUserName;
	private final String journalName;

	public DuplicateSubscriptionException(String subscriberUserName, String journalName) {
		this(subscriberUserName, journalName, String.format("The user '%s' has already subscribed to the journal '%s'.",
				subscriberUserName, journalName));
	}

	public DuplicateSubscriptionException(String subscriberUserName, String journalName, String message) {
		super(message);
		this.subscriberUserName = subscriberUserName;
		this.journalName = journalName;
	}

	public String getSubscriberUserName() {
		return subscriberUserName;
	}

	public String getJournalName() {
		return journalName;
	}

}
