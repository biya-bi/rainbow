package org.rainbow.journal.core.entities;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.entities.Trackable;

@Entity
@Table(name = "Subscriptions", uniqueConstraints = @UniqueConstraint(columnNames = { "JOURNAL_ID",
		"SUBSCRIBER_PROFILE_ID" }))
@Access(value = AccessType.PROPERTY)
public class Subscription extends Trackable<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6014159285701989243L;
	private String description;
	private Date subscriptionDate = Calendar.getInstance().getTime();
	private Journal journal;
	private Profile subscriberProfile;

	public Subscription() {
	}

	public Subscription(Long id) {
		super(id);
	}

	public Subscription(String description, Date subscriptionDate, Journal journal, Profile subscriberProfile,
			String creator, String updater, Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creator, updater, creationDate, lastUpdateDate, version, id);

		this.description = description;
		this.subscriptionDate = subscriptionDate;
		this.journal = journal;
		this.subscriberProfile = subscriberProfile;
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

	@Basic(optional = false)
	@NotNull
	@Column(name = "SUBSCRIPTION_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(Date publicationDate) {
		this.subscriptionDate = publicationDate;
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
	@JoinColumn(name = "SUBSCRIBER_PROFILE_ID", referencedColumnName = "ID", nullable = false)
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	public Profile getSubscriberProfile() {
		return subscriberProfile;
	}

	public void setSubscriberProfile(Profile profile) {
		this.subscriberProfile = profile;
	}

}