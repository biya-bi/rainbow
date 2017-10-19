package org.rainbow.security.orm.entities;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;
import org.rainbow.security.orm.audit.GroupAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "GROUPS", uniqueConstraints = @UniqueConstraint(columnNames = { "APPLICATION_ID", "NAME" }))
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Group.findAll", query = "SELECT g FROM Group g"),
		@NamedQuery(name = "Group.findByName", query = "SELECT g FROM Group g WHERE UPPER(g.name) = :name"),
		@NamedQuery(name = "Group.findById", query = "SELECT g FROM Group g WHERE g.id = :id") })
@Auditable(GroupAudit.class)
public class Group extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8068643106971691619L;
	private String name;
	private Application application;
	private String description;
	private List<User> users;
	private List<Authority> authorities;

	public Group() {
	}

	public Group(Long id) {
		super(id);
	}

	public Group(Long id, String name) {
		super(id);
		this.name = name;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 256)
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "APPLICATION_ID", nullable = false)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

	@Size(max = 512)
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JoinTable(name = "GROUP_USERS", joinColumns = {
			@JoinColumn(name = "GROUP_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "USER_ID", referencedColumnName = "ID") })
	@ManyToMany
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@JoinTable(name = "GROUP_AUTHORITIES", joinColumns = {
			@JoinColumn(name = "GROUP_ID", referencedColumnName = "ID") }, inverseJoinColumns = {
					@JoinColumn(name = "AUTHORITY_ID", referencedColumnName = "ID") })
	@ManyToMany
	public List<Authority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(List<Authority> authorities) {
		this.authorities = authorities;
	}

}
