package org.rainbow.journal.orm.entities;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Access(value = AccessType.PROPERTY)
public class Profile extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8062845674555132519L;

	private String userName;
	private String firstName;
	private String lastName;
	private Date birthDate;
	private String description;
	private String email;
	private File photo;
	private Collection<Publication> publications;
	private Collection<Journal> journals;

	public Profile() {
		super();
	}

	public Profile(Long id) {
		super(id);
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

	@NotNull
	@Column(name = "USER_NAME", nullable = false, unique = true)
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(name = "LAST_NAME", nullable = false)
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Column(name = "BIRTH_DATE")
	@Temporal(TemporalType.DATE)
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthdate) {
		this.birthDate = birthdate;
	}

	@Column(columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JoinTable(name = "profile_photo", joinColumns = @JoinColumn(name = "PROFILE_ID"), inverseJoinColumns = @JoinColumn(name = "PHOTO_ID"))
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	public File getPhoto() {
		return photo;
	}

	public void setPhoto(File photo) {
		this.photo = photo;
	}

	@XmlTransient
	@JsonIgnore
	@OneToMany(mappedBy = "publisherProfile")
	public Collection<Publication> getPublications() {
		return publications;
	}

	public void setPublications(Collection<Publication> publications) {
		this.publications = publications;
	}

	@XmlTransient
	@JsonIgnore
	@OneToMany(mappedBy = "ownerProfile")
	public Collection<Journal> getJournals() {
		return journals;
	}

	public void setJournals(Collection<Journal> journals) {
		this.journals = journals;
	}

}
