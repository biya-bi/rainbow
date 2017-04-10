package org.rainbow.journal.server.dto;

import java.util.Date;

public class PublicationDto extends TrackableDto<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7536619244468144238L;
	private String description;
	private Long fileId;
	private Date publicationDate;
	private Long journalId;
	private Long publisherProfileId;

	public PublicationDto() {
		super();
	}

	public PublicationDto(String description, Long fileId, Date publicationDate, Long journalId,
			Long publisherProfileId, String creator, String updater, Date creationDate, Date lastUpdateDate,
			long version, Long id) {
		
		super(creator, updater, creationDate, lastUpdateDate, version, id);

		this.description = description;
		this.fileId = fileId;
		this.publicationDate = publicationDate;
		this.journalId = journalId;
		this.publisherProfileId = publisherProfileId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}

	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	public Long getJournalId() {
		return journalId;
	}

	public void setJournalId(Long journalId) {
		this.journalId = journalId;
	}

	public Long getPublisherProfileId() {
		return publisherProfileId;
	}

	public void setPublisherProfileId(Long publisherProfileId) {
		this.publisherProfileId = publisherProfileId;
	}

}
