package org.rainbow.journal.server.search;

import java.util.Date;

public class SubscriptionSearchParam extends SearchParam {
	private Long journalId;
	private String journalName;
	private Date subscriptionDate;
	private Long subscriberProfileId;
	private String subscriberUserName;

	public Long getJournalId() {
		return journalId;
	}

	public void setJournalId(Long journalId) {
		this.journalId = journalId;
	}

	public String getJournalName() {
		return journalName;
	}

	public void setJournalName(String name) {
		this.journalName = name;
	}

	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date publicationDate) {
		this.subscriptionDate = publicationDate;
	}

	public Long getSubscriberProfileId() {
		return subscriberProfileId;
	}

	public void setSubscriberProfileId(Long publisherId) {
		this.subscriberProfileId = publisherId;
	}

	public String getSubscriberUserName() {
		return subscriberUserName;
	}

	public void setSubscriberUserName(String publisherUserName) {
		this.subscriberUserName = publisherUserName;
	}

}
