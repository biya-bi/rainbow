package org.rainbow.security.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.LockoutPolicy;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "LOCKOUT_POLICIES_AUDIT")
public class LockoutPolicyAudit extends AbstractPolicyAudit<LockoutPolicy> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5091769244017938984L;
	private Integer duration;
	private Short threshold;
	private Integer resetTime;
	private Short attemptWindow;

	public LockoutPolicyAudit() {
	}

	public LockoutPolicyAudit(LockoutPolicy lockoutPolicy, WriteOperation writeOperation) {
		super(lockoutPolicy, writeOperation);
		this.duration = lockoutPolicy.getDuration();
		this.threshold = lockoutPolicy.getThreshold();
		this.resetTime = lockoutPolicy.getResetTime();
		this.attemptWindow = lockoutPolicy.getAttemptWindow();
	}

	@Min(0)
	@Max(99999)
	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@Min(0)
	@Max(999)
	public Short getThreshold() {
		return threshold;
	}

	public void setThreshold(Short threshold) {
		this.threshold = threshold;
	}

	@Min(1)
	@Max(99999)
	@Column(name = "RESET_TIME")
	public Integer getResetTime() {
		return resetTime;
	}

	public void setResetTime(Integer resetTime) {
		this.resetTime = resetTime;
	}

	@Min(1)
	@Max(60)
	@NotNull
	@Column(name = "ATTEMPT_WINDOW", nullable = false)
	public Short getAttemptWindow() {
		return attemptWindow;
	}

	public void setAttemptWindow(Short attemptWindow) {
		this.attemptWindow = attemptWindow;
	}

}