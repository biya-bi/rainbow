package org.rainbow.security.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;
import org.rainbow.security.orm.audit.UserAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "USERS", uniqueConstraints = @UniqueConstraint(columnNames = { "APPLICATION_ID", "USER_NAME" }))
@NamedQueries({ @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "User.findByUserName", query = "SELECT u FROM User u WHERE u.userName = :userName"),
		@NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id") })
@Auditable(UserAudit.class)
public class User extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6132875859507188185L;
	private String userName;
	private Date lastActivityDate;
	private List<Group> groups;
	private Application application;
	private Membership membership;
	private List<Authority> authorities;

	public User() {
	}

	public User(Long id) {
		super(id);
	}

	public User(Long id, String userName, Application application) {
		super(id);
		this.userName = userName;
		this.application = application;
	}

	public User(Long id, String userName) {
		this(id, userName, null);
	}

	public User(String userName, Application application) {
		this(null, userName, application);
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

	@ManyToMany(mappedBy = "users")
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	@JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID", nullable = false)
	@ManyToOne(optional = false)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	@JoinTable(name = "USER_AUTHORITIES", joinColumns = {
			@JoinColumn(name = "USER_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID") })
	@ManyToMany
	public List<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}

}
