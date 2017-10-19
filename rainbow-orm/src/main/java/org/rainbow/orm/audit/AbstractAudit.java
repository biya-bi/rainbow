package org.rainbow.orm.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
@MappedSuperclass
public abstract class AbstractAudit<TEntity> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3944008374925682438L;
	private Long auditId;
	private WriteOperation writeOperation;
	private Date creationDate;
	private Date lastUpdateDate;
	private String creator;
	private String updater;
	private Long version;

	public AbstractAudit() {
	}

	public AbstractAudit(TEntity entity, WriteOperation writeOperation) {
		Objects.requireNonNull(entity);
		this.writeOperation = Objects.requireNonNull(writeOperation);
	}

	public AbstractAudit(TEntity entity, WriteOperation writeOperation, Date creationDate, Date lastUpdateDate, String creator,
			String updater, Long version) {
		Objects.requireNonNull(entity);
		this.writeOperation = Objects.requireNonNull(writeOperation);
		this.creationDate = creationDate;
		this.lastUpdateDate = lastUpdateDate;
		this.creator = creator;
		this.updater = updater;
		this.version = version;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUDIT_ID")
	public Long getAuditId() {
		return auditId;
	}

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
	}

	@NotNull
	@Column(name = "WRITE_OPERATION", nullable = false)
	@Enumerated(EnumType.STRING)
	public WriteOperation getWriteOperation() {
		return writeOperation;
	}

	public void setWriteOperation(WriteOperation writeOperation) {
		this.writeOperation = writeOperation;
	}

	@Column
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

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATION_DATE", nullable = false)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LAST_UPDATE_DATE", nullable = false)
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Long getVersion() {
		return this.version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((auditId == null) ? 0 : auditId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAudit<?> other = (AbstractAudit<?>) obj;
		if (auditId == null) {
			if (other.auditId != null)
				return false;
		} else if (!auditId.equals(other.auditId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[ auditId=" + getAuditId() + " ]";
	}
}
