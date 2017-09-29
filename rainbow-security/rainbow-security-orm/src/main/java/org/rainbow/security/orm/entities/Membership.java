package org.rainbow.security.orm.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "MEMBERSHIPS"/*
							 * , uniqueConstraints
							 * = @UniqueConstraint(columnNames =
							 * {"APPLICATION_ID", "EMAIL"})
							 */)
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Membership.findAll", query = "SELECT m FROM Membership m"),
		@NamedQuery(name = "Membership.findById", query = "SELECT m FROM Membership m WHERE (m.user IS NOT NULL) AND m.user.id = :id"),
		@NamedQuery(name = "Membership.findByIsEnabled", query = "SELECT m FROM Membership m WHERE m.enabled = :enabled"),
		@NamedQuery(name = "Membership.findByIsLockedOut", query = "SELECT m FROM Membership m WHERE m.locked = :locked") })
public class Membership implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1588460705004075536L;
	private User user;
	private String password;
	private boolean enabled = true;
	private Date creationDate;
	private boolean locked;
	private String description;
	private Date lastPasswordChangeDate;
	private Date lastLockoutDate;
	private short failedPasswordAttemptCount;
	private Date failedPasswordAttemptWindowStart;
	private short failedRecoveryAttemptsCount;
	private Date failedRecoveryAttemptsWindowStart;
	private List<PasswordHistory> passwordHistories;
	private String email;
	private String phone;
	private boolean encrypted;
	private List<RecoveryInformation> recoveryInformation;
	private List<LoginHistory> loginHistories;

	public Membership() {
	}

	public Membership(User user) {
		this.user = user;
	}

	public Membership(User user, String password, String description, boolean enabled, boolean locked) {
		this.user = user;
		this.password = password;
		this.description = description;
		this.enabled = enabled;
		this.locked = locked;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " [user=" + user + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
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
		if (!(obj instanceof Membership)) {
			return false;
		}
		Membership other = (Membership) obj;
		if (user == null) {
			if (other.user != null) {
				return false;
			}
		} else if (!user.equals(other.user)) {
			return false;
		}
		return true;
	}

	@NotNull
	@Column(name = "PASSWORD", nullable = false)
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Id
	@JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
	@OneToOne(optional = false)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	@Column(name = "CREATION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	@OneToMany(mappedBy = "membership", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<PasswordHistory> getPasswordHistories() {
		return passwordHistories;
	}

	public void setPasswordHistories(List<PasswordHistory> passwordHistories) {
		this.passwordHistories = passwordHistories;
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

	@OneToMany(mappedBy = "membership", cascade = CascadeType.ALL, orphanRemoval = true)
	public List<RecoveryInformation> getRecoveryInformation() {
		return recoveryInformation;
	}

	public void setRecoveryInformation(List<RecoveryInformation> recoveryInformation) {
		this.recoveryInformation = recoveryInformation;
	}

	@OneToMany(mappedBy = "membership", cascade = CascadeType.REMOVE)
	public List<LoginHistory> getLoginHistories() {
		return loginHistories;
	}

	public void setLoginHistories(List<LoginHistory> loginHistories) {
		this.loginHistories = loginHistories;
	}

}
