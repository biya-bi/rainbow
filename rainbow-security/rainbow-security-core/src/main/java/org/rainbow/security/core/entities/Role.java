/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.entities;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "ROLES", uniqueConstraints = @UniqueConstraint(columnNames = { "APPLICATION_ID", "NAME" }))
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Role.findAll", query = "SELECT r FROM Role r"),
		@NamedQuery(name = "Role.findByName", query = "SELECT r FROM Role r WHERE UPPER(r.name) = :name"),
		@NamedQuery(name = "Role.findById", query = "SELECT r FROM Role r WHERE r.id = :id") })
public class Role extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6126524561303146659L;
	private String name;
	private Application application;
	private String description;
	private List<User> users;

	public Role() {
	}

	public Role(Long id) {
		super(id);
	}

	public Role(Long id, String name) {
		super(id);
		this.name = name;
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

	@XmlTransient
	@OneToMany(mappedBy = "role")
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
