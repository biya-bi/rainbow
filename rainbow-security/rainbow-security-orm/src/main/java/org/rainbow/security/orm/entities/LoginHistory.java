package org.rainbow.security.orm.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.entities.AbstractEntity;

@Entity
@Table(name = "LOGIN_HISTORIES")
public class LoginHistory extends AbstractEntity<LoginHistoryId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8700850380036154774L;
	private LoginHistoryId id;
	private Date loginDate;
	private Membership membership;

	@EmbeddedId
	public LoginHistoryId getId() {
		return this.id;
	}

	public void setId(LoginHistoryId id) {
		this.id = id;
	}

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "LOGIN_DATE", nullable = false)
	public Date getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(Date loginDate) {
		this.loginDate = loginDate;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "MEMBERSHIP_ID", nullable = false, insertable = false, updatable = false)
	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}
}
