package org.rainbow.journal.server.dto;

import java.io.Serializable;
import java.util.Date;

import org.rainbow.orm.entities.Identifiable;

public class TrackableDto<T extends Serializable> extends Identifiable<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7695816802149184049L;
	private String creator;
	private String updater;
	private Date creationDate;
	private Date lastUpdateDate;
	private long version;

	public TrackableDto() {
	}

	public TrackableDto(T id) {
		super(id);
	}

	public TrackableDto(Date creationDate, Date lastUpdateDate) {
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
	}

	public TrackableDto(Date creationDate, Date lastUpdateDate, long version, T id) {
		super(id);
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
	}

	public TrackableDto(String creator, String updater, Date creationDate, Date lastUpdateDate) {
		this.creator = creator;
		this.updater = updater;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
	}

	public TrackableDto(String creator, String updater, Date creationDate, Date lastUpdateDate, long version, T id) {
		super(id);
		this.creator = creator;
		this.updater = updater;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getUpdater() {
		return updater;
	}

	public void setUpdater(String updater) {
		this.updater = updater;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
