package org.rainbow.security.orm.entities;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
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
import org.rainbow.security.orm.audit.AuthorityAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "AUTHORITIES", uniqueConstraints = @UniqueConstraint(columnNames = { "APPLICATION_ID", "NAME" }))
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Authority.findAll", query = "SELECT a FROM Authority a"),
		@NamedQuery(name = "Authority.findByName", query = "SELECT a FROM Authority a WHERE UPPER(a.name) = :name"),
		@NamedQuery(name = "Authority.findById", query = "SELECT a FROM Authority a WHERE a.id = :id") })
@Auditable(AuthorityAudit.class)
public class Authority extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5106554468275694457L;
	private String name;
	private Application application;
	private String description;
	private List<Group> groups;
	private List<User> users;

	public Authority() {
	}

	public Authority(String name) {
		this.name = name;
	}

	public Authority(Long id, String name) {
		super(id);
		this.name = name;
	}

	public Authority(Long id) {
		super(id);
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

	@ManyToMany(mappedBy = "authorities")
	public List<Group> getGroups() {
		return groups;
	}

	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}

	@ManyToMany(mappedBy = "authorities")
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
