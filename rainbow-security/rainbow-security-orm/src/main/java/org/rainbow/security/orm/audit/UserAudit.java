package org.rainbow.security.orm.audit;

import java.util.Date;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;
import org.rainbow.security.orm.entities.User;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "USERS_AUDIT")
public class UserAudit extends AbstractAuditableEntityAudit<User, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4641908175812876135L;
	private String userName;
	private Date lastActivityDate;
	private Long applicationId;
	private Long membershipId;

	public UserAudit() {
	}

	public UserAudit(User user, WriteOperation writeOperation) {
		super(user, writeOperation);
		Objects.requireNonNull(user.getApplication());
		Objects.requireNonNull(user.getMembership());
		this.userName = user.getUserName();
		this.lastActivityDate = user.getLastActivityDate();
		this.applicationId = user.getApplication().getId();
		this.membershipId = user.getMembership().getId();
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(name = "USER_NAME", nullable = false)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "LAST_ACTIVITY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastActivityDate() {
		return lastActivityDate;
	}

	public void setLastActivityDate(Date lastActivityDate) {
		this.lastActivityDate = lastActivityDate;
	}

	@NotNull
	@Column(name = "APPLICATION_ID", nullable = false)
	public Long getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(Long applicationId) {
		this.applicationId = applicationId;
	}

	@Column(name = "MEMBERSHIP_ID")
	public Long getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(Long membershipId) {
		this.membershipId = membershipId;
	}

}
