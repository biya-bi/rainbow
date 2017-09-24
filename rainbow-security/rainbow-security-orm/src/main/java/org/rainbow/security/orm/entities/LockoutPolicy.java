package org.rainbow.security.orm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "LOCKOUT_POLICIES")
public class LockoutPolicy extends AccountPolicy {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1315964833824332373L;
	private Application application;
	private Integer duration;
	private Short threshold = 0;
	private Integer resetTime;
	private Short attemptWindow = 10;

	@Id
	@OneToOne
	@JoinColumn(name = "APPLICATION_ID")
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	/**
	 * Get the account lockout duration.
	 * 
	 * This security setting determines the number of minutes a locked-out
	 * account remains locked out before automatically becoming unlocked. The
	 * available range is from 0 minutes through 99,999 minutes. If you set the
	 * account lockout duration to 0, the account will be locked out until an
	 * administrator explicitly unlocks it.
	 * 
	 * If an account lockout threshold is defined, the account lockout duration
	 * must be greater than or equal to the reset time.
	 * 
	 * Default: None, because this policy setting only has meaning when an
	 * Account lockout threshold is specified.
	 * 
	 * @return the account lockout duration
	 */
	@Min(0)
	@Max(99999)
	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	/**
	 * Get the account lockout threshold.
	 * 
	 * This security setting determines the number of failed logon attempts that
	 * causes a user account to be locked out. A locked-out account cannot be
	 * used until it is reset by an administrator or until the lockout duration
	 * for the account has expired. You can set a value between 0 and 999 failed
	 * logon attempts. If you set the value to 0, the account will never be
	 * locked out.
	 * 
	 * Default: 0.
	 * 
	 * @return the account lockout threshold
	 */
	@Min(0)
	@Max(999)
	public Short getThreshold() {
		return threshold;
	}

	public void setThreshold(Short threshold) {
		this.threshold = threshold;
	}

	/**
	 * Get the account lockout reset time.
	 * 
	 * This security setting determines the number of minutes that must elapse
	 * after a failed logon (or recovery) attempt before the failed logon (or
	 * recovery) attempt counter is reset to 0 bad logon (or recovery) attempts.
	 * The available range is 1 minute to 99,999 minutes.
	 * 
	 * If an account lockout threshold is defined, this reset time must be less
	 * than or equal to the account lockout duration.
	 * 
	 * Default: None, because this policy setting only has meaning when an
	 * account lockout threshold is specified.
	 * 
	 * @return the account lockout reset time
	 */
	@Min(1)
	@Max(99999)
	@Column(name = "RESET_TIME")
	public Integer getResetTime() {
		return resetTime;
	}

	public void setResetTime(Integer resetTime) {
		this.resetTime = resetTime;
	}

	/**
	 * Get the attempt window.
	 * 
	 * This security setting determines the number of minutes within which
	 * failed password or account recovery attempts may cause an account to be
	 * locked out. The available range is 1 minute to 60 minutes.
	 * 
	 * @return
	 */
	@Min(1)
	@Max(60)
	@NotNull
	@Column(name = "ATTEMPT_WINDOW", nullable = false)
	public Short getAttemptWindow() {
		return attemptWindow;
	}

	public void setAttemptWindow(Short passwordAttemptWindow) {
		this.attemptWindow = passwordAttemptWindow;
	}
}
