package org.rainbow.journal.core.entities;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

import org.rainbow.core.entities.Trackable;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Journals")
@Access(value = AccessType.PROPERTY)
public class Journal extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8004481959339286393L;
	private String name;
	private String description;
	private File photo;
	private String tag;
	private boolean active = true;
	private Collection<Publication> publications;
	private Profile ownerProfile;

	public Journal() {
	}

	public Journal(Long id) {
		super(id);
	}

	public Journal(String name, String description, File photo, String tag, boolean active, Profile ownerProfile,
			String creator, String updater, Date creationDate, Date lastUpdateDate, long version, Long id) {

		super(creator, updater, creationDate, lastUpdateDate, version, id);

		this.name = name;
		this.description = description;
		this.photo = photo;
		this.tag = tag;
		this.active = active;
		this.ownerProfile = ownerProfile;
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
	@Column(length = 255, nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JoinTable(name = "Journals_Photos", joinColumns = @JoinColumn(name = "JOURNAL_ID"), inverseJoinColumns = @JoinColumn(name = "PHOTO_ID"))
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	public File getPhoto() {
		return photo;
	}

	public void setPhoto(File photo) {
		this.photo = photo;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@NotNull
	@Column(nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@XmlTransient
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "journal", orphanRemoval = true)
	public Collection<Publication> getPublications() {
		return publications;
	}

	public void setPublications(Collection<Publication> publications) {
		this.publications = publications;
	}

	@NotNull
	@JoinColumn(name = "OWNER_PROFILE_ID", referencedColumnName = "ID", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public Profile getOwnerProfile() {
		return ownerProfile;
	}

	public void setOwnerProfile(Profile ownerProfile) {
		this.ownerProfile = ownerProfile;
	}

}