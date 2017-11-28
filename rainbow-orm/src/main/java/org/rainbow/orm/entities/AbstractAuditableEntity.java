package org.rainbow.orm.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

/**
 *
 * @author Biya-Bi
 * @param <T>
 *            The type of the ID
 */
@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public abstract class AbstractAuditableEntity<T extends Serializable> extends AbstractEntity<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7322367081639467874L;
	private String creator;
	private String updater;
	private Date creationDate;
	private Date lastUpdateDate;
	private long version;

	public AbstractAuditableEntity() {
	}

	public AbstractAuditableEntity(T id) {
		super(id);
	}

	@Id
	@Override
	public T getId() {
		return super.getId();
	}

	@Override
	public void setId(T id) {
		super.setId(id);
	}

	public AbstractAuditableEntity(Date creationDate, Date lastUpdateDate) {
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
	}

	public AbstractAuditableEntity(Date creationDate, Date lastUpdateDate, long version, T id) {
		super(id);
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
	}

	public AbstractAuditableEntity(String creator, String updater, Date creationDate, Date lastUpdateDate) {
		this.creator = creator;
		this.updater = updater;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
	}

	public AbstractAuditableEntity(String creator, String updater, Date creationDate, Date lastUpdateDate, long version,
			T id) {
		super(id);
		this.creator = creator;
		this.updater = updater;
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.version = version;
	}

	@Column(updatable = false)
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", updatable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE_DATE")
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Version
	public long getVersion() {
		return this.version;
	}

	public void setVersion(long version) {
		this.version = version;
	}

}
