package org.rainbow.journal.server.dto;

import java.util.Date;

public class SubscriptionDto extends AuditableDto<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1411756707226822430L;
	private String description;
	private Date subscriptionDate;
	private Long journalId;
	private Long subscriberProfileId;

	public SubscriptionDto() {
		super();
	}

	public SubscriptionDto(String description, Date subscriptionDate, Long journalId, Long subscriberProfileId,
			String creator, String updater, Date creationDate, Date lastUpdateDate, long version, Long id) {

		super(creator, updater, creationDate, lastUpdateDate, version, id);

		this.description = description;
		this.subscriptionDate = subscriptionDate;
		this.journalId = journalId;
		this.subscriberProfileId = subscriberProfileId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date publicationDate) {
		this.subscriptionDate = publicationDate;
	}

	public Long getJournalId() {
		return journalId;
	}

	public void setJournalId(Long journalId) {
		this.journalId = journalId;
	}

	public Long getSubscriberProfileId() {
		return subscriberProfileId;
	}

	public void setSubscriberProfileId(Long publisherProfileId) {
		this.subscriberProfileId = publisherProfileId;
	}

}
