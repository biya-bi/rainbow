package org.rainbow.security.orm.audit;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.Membership;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "MEMBERSHIPS_AUDIT")
public class MembershipAudit extends AbstractAuditableEntityAudit<Membership, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2219439981927472352L;
	private String password;
	private Long userId;
	private boolean enabled;
	private boolean locked;
	private String description;
	private Date lastPasswordChangeDate;
	private Date lastLockoutDate;
	private short failedPasswordAttemptCount;
	private Date failedPasswordAttemptWindowStart;
	private short failedRecoveryAttemptsCount;
	private Date failedRecoveryAttemptsWindowStart;
	private String email;
	private String phone;
	private boolean encrypted;

	public MembershipAudit() {
	}

	public MembershipAudit(Membership membership, WriteOperation writeOperation) {
		super(membership, writeOperation);
		Objects.requireNonNull(membership.getUser());
		this.password = membership.getPassword();
		this.userId = membership.getUser().getId();
		this.enabled = membership.isEnabled();
		this.locked = membership.isLocked();
		this.description = membership.getDescription();
		this.lastPasswordChangeDate = membership.getLastPasswordChangeDate();
		this.lastLockoutDate = membership.getLastLockoutDate();
		this.failedPasswordAttemptCount = membership.getFailedPasswordAttemptCount();
		this.failedPasswordAttemptWindowStart = membership.getFailedPasswordAttemptWindowStart();
		this.failedRecoveryAttemptsCount = membership.getFailedRecoveryAttemptsCount();
		this.failedRecoveryAttemptsWindowStart = membership.getFailedRecoveryAttemptsWindowStart();
		this.email = membership.getEmail();
		this.phone = membership.getPhone();
		this.encrypted = membership.isEncrypted();
	}

	@NotNull
	@Column(name = "PASSWORD", nullable = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "USER_ID", nullable = false)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Basic(optional = false)
	@NotNull
	@Column(name = "ENABLED")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Column(name = "LOCKED")
	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "LAST_PWD_CHANGE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastPasswordChangeDate() {
		return lastPasswordChangeDate;
	}

	public void setLastPasswordChangeDate(Date lastPasswordChangeDate) {
		this.lastPasswordChangeDate = lastPasswordChangeDate;
	}

	@Column(name = "LAST_LOCK_OUT_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastLockoutDate() {
		return lastLockoutDate;
	}

	public void setLastLockoutDate(Date lastLockoutDate) {
		this.lastLockoutDate = lastLockoutDate;
	}

	@Column(name = "FAILED_PWD_ATMPT_CNT")
	public short getFailedPasswordAttemptCount() {
		return failedPasswordAttemptCount;
	}

	public void setFailedPasswordAttemptCount(short failedPasswordAttemptCount) {
		this.failedPasswordAttemptCount = failedPasswordAttemptCount;
	}

	@Column(name = "FAILED_PWD_ATMPT_WIN_START")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFailedPasswordAttemptWindowStart() {
		return failedPasswordAttemptWindowStart;
	}

	public void setFailedPasswordAttemptWindowStart(Date failedPasswordAttemptWindowStart) {
		this.failedPasswordAttemptWindowStart = failedPasswordAttemptWindowStart;
	}

	@Min(0)
	@Column(name = "FAILED_REVRY_ATMPT_CNT")
	public short getFailedRecoveryAttemptsCount() {
		return failedRecoveryAttemptsCount;
	}

	public void setFailedRecoveryAttemptsCount(short failedRecoveryAttemptsCount) {
		this.failedRecoveryAttemptsCount = failedRecoveryAttemptsCount;
	}

	@Column(name = "FAILED_REVRY_ATMPT_WIN_START")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFailedRecoveryAttemptsWindowStart() {
		return failedRecoveryAttemptsWindowStart;
	}

	public void setFailedRecoveryAttemptsWindowStart(Date failedRecoveryAttemptsWindowStart) {
		this.failedRecoveryAttemptsWindowStart = failedRecoveryAttemptsWindowStart;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public boolean isEncrypted() {
		return encrypted;
	}

	public void setEncrypted(boolean encryted) {
		this.encrypted = encryted;
	}

}
