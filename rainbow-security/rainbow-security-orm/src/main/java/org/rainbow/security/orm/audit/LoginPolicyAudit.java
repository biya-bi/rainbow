package org.rainbow.security.orm.audit;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.LoginPolicy;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "LOGIN_POLICIES_AUDIT")
public class LoginPolicyAudit extends AbstractPolicyAudit<LoginPolicy> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4949629819347825352L;
	private Short threshold;

	public LoginPolicyAudit() {
	}

	public LoginPolicyAudit(LoginPolicy loginPolicy, WriteOperation writeOperation) {
		super(loginPolicy, writeOperation);
		this.threshold = loginPolicy.getThreshold();
	}

	@Min(0)
	@Max(999)
	public Short getThreshold() {
		return threshold;
	}

	public void setThreshold(Short threshold) {
		this.threshold = threshold;
	}

}