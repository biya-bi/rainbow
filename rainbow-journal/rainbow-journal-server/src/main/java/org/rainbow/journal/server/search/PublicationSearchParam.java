package org.rainbow.journal.server.search;

import java.util.Date;

public class PublicationSearchParam extends SearchParam {
	private Long journalId;
	private String journalName;
	private Date publicationDate;
	private Long publisherProfileId;
	private String publisherUserName;

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

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public Long getPublisherProfileId() {
		return publisherProfileId;
	}

	public void setPublisherProfileId(Long publisherId) {
		this.publisherProfileId = publisherId;
	}

	public String getPublisherUserName() {
		return publisherUserName;
	}

	public void setPublisherUserName(String publisherUserName) {
		this.publisherUserName = publisherUserName;
	}

}
