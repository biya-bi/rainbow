package org.rainbow.security.orm.audit;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.PasswordHistory;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PASSWORD_HISTORIES_AUDIT")
public class PasswordHistoryAudit extends AbstractAuditableEntityAudit<PasswordHistory, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7726104189528716928L;
	private Long membershipId;
	private String password;
	private Date changeDate;

	public PasswordHistoryAudit() {
	}

	public PasswordHistoryAudit(PasswordHistory passwordHistory, WriteOperation writeOperation) {
		super(passwordHistory, writeOperation);
		Objects.requireNonNull(passwordHistory.getMembership());
		this.membershipId = passwordHistory.getMembership().getId();
		this.password = passwordHistory.getPassword();
		this.changeDate = passwordHistory.getChangeDate();
	}

	@NotNull
	@Column(name = "MEMBERSHIP_ID", nullable = false)
	public Long getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(Long membershipId) {
		this.membershipId = membershipId;
	}

	@NotNull
	@Column(name = "PASSWORD", nullable = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "CHANGE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getChangeDate() {
		return changeDate;
	}

	public void setChangeDate(Date changeDate) {
		this.changeDate = changeDate;
	}

}
