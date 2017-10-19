package org.rainbow.security.orm.audit;

import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.AbstractPolicy;

/**
 *
 * @author Biya-Bi
 */
@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public abstract class AbstractPolicyAudit<T extends AbstractPolicy> extends AbstractAuditableEntityAudit<T, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1961605072755793798L;
	private Long applicationId;

	public AbstractPolicyAudit() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "AUDIT_ID")
	@Override
	public Long getAuditId() {
		return super.getAuditId();
	}

	@Override
	public void setAuditId(Long auditId) {
		super.setAuditId(auditId);
	}

	public AbstractPolicyAudit(T policy, WriteOperation writeOperation) {
		super(policy, writeOperation);
		Objects.requireNonNull(policy.getApplication());
		this.applicationId = policy.getApplication().getId();
	}

	@NotNull
	@Column(name = "APPLICATION_ID")
	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}
}