package org.rainbow.journal.server.dto;

import java.util.Date;

public class JournalDto extends TrackableDto<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1337841141662535757L;
	private String name;
	private String description;
	private Long photoId;
	private String tag;
	private boolean active;
	private Long ownerProfileId;

	public JournalDto() {
		super();
	}

	public JournalDto(String name, String description, Long photoId, String tag, boolean active, Long ownerProfileId,
			String creator, String updater, Date creationDate, Date lastUpdateDate, long version, Long id) {

		super(creator, updater, creationDate, lastUpdateDate, version, id);

		this.name = name;
		this.description = description;
		this.photoId = photoId;
		this.tag = tag;
		this.active = active;
		this.ownerProfileId = ownerProfileId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getPhotoId() {
		return photoId;
	}

	public void setPhotoId(Long photoId) {
		this.photoId = photoId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getOwnerProfileId() {
		return ownerProfileId;
	}

	public void setOwnerProfileId(Long ownerProfileId) {
		this.ownerProfileId = ownerProfileId;
	}

}
