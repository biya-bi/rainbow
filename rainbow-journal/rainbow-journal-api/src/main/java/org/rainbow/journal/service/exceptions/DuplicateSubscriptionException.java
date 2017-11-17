package org.rainbow.journal.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateSubscriptionException extends RainbowJournalException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5809793048063548534L;
	private final Long subscriberProfileId;
	private final Long journalId;

	public DuplicateSubscriptionException(Long subscriberProfileId, Long journalId) {
		this(subscriberProfileId, journalId, String.format("The user with profile Id '%d' has already subscribed to the journal with Id is '%d'.",
				subscriberProfileId, journalId));
	}

	public DuplicateSubscriptionException(Long subscriberProfileId, Long journalId, String message) {
		super(message);
		this.subscriberProfileId = subscriberProfileId;
		this.journalId = journalId;
	}

	public Long getSubscriberProfileId() {
		return subscriberProfileId;
	}

	public Long getJournalId() {
		return journalId;
	}

}
