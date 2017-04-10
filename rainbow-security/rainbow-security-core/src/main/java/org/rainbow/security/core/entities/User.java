/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.rainbow.core.entities.Trackable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "USERS", uniqueConstraints = @UniqueConstraint(columnNames = { "APPLICATION_ID", "USER_NAME" }))
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
		@NamedQuery(name = "User.findByUserName", query = "SELECT u FROM User u WHERE u.userName = :userName"),
		@NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id") })
public class User extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6132875859507188185L;
	private String userName;
	private Date lastActivityDate;
	private List<Group> groups;
	private Application application;
	private Membership membership;
	private Token token;
	private Role role;

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

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
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
	@XmlTransient
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

	@OneToOne(mappedBy = "user", cascade = { CascadeType.ALL })
	public Membership getMembership() {
		return membership;
	}

	public void setMembership(Membership membership) {
		this.membership = membership;
	}

	@OneToOne(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	public Token getToken() {
		return token;
	}

	public void setToken(Token token) {
		this.token = token;
	}

	@ManyToOne
	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

}
