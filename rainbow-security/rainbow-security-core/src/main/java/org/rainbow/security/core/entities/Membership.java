/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.entities;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
		@NamedQuery(name = "Membership.findByIsLockedOut", query = "SELECT m FROM Membership m WHERE m.lockedOut = :lockedOut") })
public class Membership implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1588460705004075536L;
	private User user;
	private String password;
	private String passwordQuestion;
	private String passwordQuestionAnswer;
	private boolean enabled = true;
	private Date creationDate;
	private boolean lockedOut;
	private String description;
	private Application application;
	private Date lastLoginDate;
	private Date lastPasswordChangeDate;
	private Date lastLockoutDate;
	private int failedPasswordAttemptCount;
	private Date failedPasswordAttemptWindowStart;
	private int failedPasswordQuestionAnswerAttemptCount;
	private Date failedPasswordAnswerAttemptWindowStart;
	private List<PasswordHistory> passwordHistories;
	private String email;
	private String phone;
	private boolean encrypted;

	public Membership() {
	}

	public Membership(User user) {
		this.user = user;
	}

	public Membership(User user, String password, String description, boolean isApproved, boolean isLockedOut) {
		this.user = user;
		this.password = password;
		this.description = description;
		this.enabled = isApproved;
		this.lockedOut = isLockedOut;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (user != null && user.getId() != null ? user.getId().hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// Warning - this method won't work in the case the id fields are not
		// set
		if (!(object instanceof Membership)) {
			return false;
		}
		Membership other = (Membership) object;
		if ((this.user == null && other.user != null) || (this.user != null && !this.user.equals(other.user))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[ id=" + (user == null || user.getId() == null ? 0 : user.getId()) + " ]";
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

	@Size(max = 256)
	@Column(name = "PASSWORD_QUESTION")
	public String getPasswordQuestion() {
		return passwordQuestion;
	}

	public void setPasswordQuestion(String passwordQuestion) {
		this.passwordQuestion = passwordQuestion;
	}

	@Size(max = 128)
	@Column(name = "PASSWORD_QUESTION_ANSWER")
	public String getPasswordQuestionAnswer() {
		return passwordQuestionAnswer;
	}

	public void setPasswordQuestionAnswer(String passwordQuestionAnswer) {
		this.passwordQuestionAnswer = passwordQuestionAnswer;
	}

	@Basic(optional = false)
	@NotNull
	@Column(name = "ENABLED")
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean approved) {
		this.enabled = approved;
	}

	@Column(name = "CREATION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Column(name = "LOCKED_OUT")
	public boolean isLockedOut() {
		return lockedOut;
	}

	public void setLockedOut(boolean lockedOut) {
		this.lockedOut = lockedOut;
	}

	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@NotNull
	@JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID", nullable = false)
	@OneToOne(optional = false)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Column(name = "LAST_LOGIN_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
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
	public int getFailedPasswordAttemptCount() {
		return failedPasswordAttemptCount;
	}

	public void setFailedPasswordAttemptCount(int failedPasswordAttemptCount) {
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

	@Column(name = "FAILED_PWD_QUES_ANS_ATMPT_CNT")
	public int getFailedPasswordQuestionAnswerAttemptCount() {
		return failedPasswordQuestionAnswerAttemptCount;
	}

	public void setFailedPasswordQuestionAnswerAttemptCount(int failedPasswordQuestionAnswerAttemptCount) {
		this.failedPasswordQuestionAnswerAttemptCount = failedPasswordQuestionAnswerAttemptCount;
	}

	@Column(name = "FAILED_PWD_ANS_ATPT_WIN_START")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getFailedPasswordAnswerAttemptWindowStart() {
		return failedPasswordAnswerAttemptWindowStart;
	}

	public void setFailedPasswordAnswerAttemptWindowStart(Date failedPasswordAnswerAttemptWindowStart) {
		this.failedPasswordAnswerAttemptWindowStart = failedPasswordAnswerAttemptWindowStart;
	}

	@OneToMany(mappedBy = "membership", cascade = CascadeType.REMOVE)
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

}
