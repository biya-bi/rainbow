package org.rainbow.security.orm.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.audit.Auditable;
import org.rainbow.security.orm.audit.LoginPolicyAudit;

@Entity
@Table(name = "LOGIN_POLICIES")
@Access(value = AccessType.PROPERTY)
@Auditable(LoginPolicyAudit.class)
public class LoginPolicy extends AbstractPolicy {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1428217396677807040L;
	private Short threshold;

	@Min(1)
	@Max(999)
	@NotNull
	@Column(nullable = false)
	public Short getThreshold() {
		return threshold;
	}

	public void setThreshold(Short threshold) {
		this.threshold = threshold;
	}

}
