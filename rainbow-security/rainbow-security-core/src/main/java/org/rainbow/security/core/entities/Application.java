/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.entities;

import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
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
@Table(name = "APPLICATIONS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Application.findAll", query = "SELECT a FROM Application a"),
		@NamedQuery(name = "Application.findById", query = "SELECT a FROM Application a WHERE a.id = :id"),
		@NamedQuery(name = "Application.findByName", query = "SELECT a FROM Application a WHERE a.name = :name") })
public class Application extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1528777423855662986L;
	private String name;
	private String description;
	private List<User> users;
	private PasswordPolicy passwordPolicy;
	private TokenPolicy tokenPolicy;
	private List<Group> groups;
	private List<Authority> authorities;

	public Application() {
	}

	public Application(Long id, String name) {
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
	@Column(name = "NAME", unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Size(max = 256)
	@Column(name = "DESCRIPTION")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "application")
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	@NotNull
	@OneToOne(mappedBy = "application", cascade = { CascadeType.ALL })
	public PasswordPolicy getPasswordPolicy() {
		return passwordPolicy;
	}

	public void setPasswordPolicy(PasswordPolicy passwordPolicy) {
		this.passwordPolicy = passwordPolicy;
	}

	@NotNull
	@OneToOne(mappedBy = "application", cascade = { CascadeType.ALL })
	public TokenPolicy getTokenPolicy() {
		return tokenPolicy;
	}

	public void setTokenPolicy(TokenPolicy tokenPolicy) {
		this.tokenPolicy = tokenPolicy;
	}

	@OneToMany(mappedBy = "application")
	public List<Group> getRoles() {
		return groups;
	}

	public void setRoles(List<Group> groups) {
		this.groups = groups;
	}

	@OneToMany(mappedBy = "application")
	public List<Authority> getPermissions() {
		return authorities;
	}

	public void setPermissions(List<Authority> authorities) {
		this.authorities = authorities;
	}

}
