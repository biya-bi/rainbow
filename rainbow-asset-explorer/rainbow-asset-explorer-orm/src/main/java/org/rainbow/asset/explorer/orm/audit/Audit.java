package org.rainbow.asset.explorer.orm.audit;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Biya-Bi
 */
@MappedSuperclass
public abstract class Audit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5735060810921729766L;
	private Long auditId;
	private WriteOperation writeOperation;

	public Audit() {
	}

	public Audit(WriteOperation writeOperation) {
		this.writeOperation = writeOperation;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((auditId == null) ? 0 : auditId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Audit)) {
			return false;
		}
		Audit other = (Audit) obj;
		if (auditId == null) {
			if (other.auditId != null) {
				return false;
			}
		} else if (!auditId.equals(other.auditId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[ auditId=" + getAuditId() + " ]";
	}
}
