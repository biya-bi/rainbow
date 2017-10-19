package org.rainbow.security.orm.audit;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.Application;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "APPLICATIONS_AUDIT")
public class ApplicationAudit extends AbstractAuditableEntityAudit<Application, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4399226066091444519L;
	private String name;
	private String description;
	private Long passwordPolicyId;
	private Long lockoutPolicyId;
	private Long loginPolicyId;

	public ApplicationAudit() {
	}

	public ApplicationAudit(Application application, WriteOperation writeOperation) {
		super(application, writeOperation);
		this.name = application.getName();
		this.description = application.getDescription();
		this.passwordPolicyId = application.getPasswordPolicy() != null ? application.getPasswordPolicy().getId()
				: null;
		this.lockoutPolicyId = application.getLockoutPolicy() != null ? application.getLockoutPolicy().getId() : null;
		this.loginPolicyId = application.getLoginPolicy() != null ? application.getLoginPolicy().getId() : null;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Size(max = 256)
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "PASSWORD_POLICY_ID")
	public Long getPasswordPolicyId() {
		return passwordPolicyId;
	}

	public void setPasswordPolicyId(Long passwordPolicyId) {
		this.passwordPolicyId = passwordPolicyId;
	}

	@Column(name = "LOCKOUT_POLICY_ID")
	public Long getLockoutPolicyId() {
		return lockoutPolicyId;
	}

	public void setLockoutPolicyId(Long lockoutPolicyId) {
		this.lockoutPolicyId = lockoutPolicyId;
	}

	@Column(name = "LOGIN_POLICY_ID")
	public Long getLoginPolicyId() {
		return loginPolicyId;
	}

	public void setLoginPolicyId(Long loginPolicyId) {
		this.loginPolicyId = loginPolicyId;
	}

}
