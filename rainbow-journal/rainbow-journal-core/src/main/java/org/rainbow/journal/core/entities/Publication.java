package org.rainbow.journal.core.entities;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.entities.Trackable;

@Entity
@Table(name = "Publications")
@Access(value = AccessType.PROPERTY)
public class Publication extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3040918265330594746L;
	private String description;
	private File file;
	private Date publicationDate = Calendar.getInstance().getTime();
	private Journal journal;
	private Profile publisherProfile;

	public Publication() {
	}

	public Publication(Long id) {
		super(id);
	}

	public Publication(String description, File file, Date publicationDate, Journal journal, Profile publisherProfile,
			String creator, String updater, Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creator, updater, creationDate, lastUpdateDate, version, id);
		this.description = description;
		this.file = file;
		this.publicationDate = publicationDate;
		this.journal = journal;
		this.publisherProfile = publisherProfile;
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

	@Column(columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@JoinTable(name = "Publications_Files", joinColumns = @JoinColumn(name = "PUBLICATION_ID"), inverseJoinColumns = @JoinColumn(name = "FILE_ID"))
	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Basic(optional = false)
	@NotNull
	@Column(name = "PUBLICATION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(Date publicationDate) {
		this.publicationDate = publicationDate;
	}

	@NotNull
	@JoinColumn(name = "JOURNAL_ID", referencedColumnName = "ID", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public Journal getJournal() {
		return journal;
	}

	public void setJournal(Journal journal) {
		this.journal = journal;
	}

	@NotNull
	@JoinColumn(name = "PUBLISHER_PROFILE_ID", referencedColumnName = "ID", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public Profile getPublisherProfile() {
		return publisherProfile;
	}

	public void setPublisherProfile(Profile profile) {
		this.publisherProfile = profile;
	}

}