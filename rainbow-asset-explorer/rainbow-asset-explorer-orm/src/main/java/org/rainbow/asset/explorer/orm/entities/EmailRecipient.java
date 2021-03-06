package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.audit.EmailRecipientAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "EMAIL_RECIPIENT")

@Auditable(EmailRecipientAudit.class)
public class EmailRecipient extends AbstractNumericIdAuditableEntity<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6645441710437753976L;
	private String name;
	private String email;
	private Locale locale;
	private String description;
	private List<Alert> alerts;

	public EmailRecipient() {
	}

	public EmailRecipient(Integer id) {
		super(id);
	}

	public EmailRecipient(String name, String email) {
		this.name = name;
		this.email = email;
	}

	public EmailRecipient(String name, String email, Locale locale) {
		this.name = name;
		this.email = email;
		this.locale = locale;
	}

	public EmailRecipient(String name, String email, Locale locale, String description, String createdBy,
			String updatedBy, Date creationDate, Date lastUpdateDate, long version, Integer id) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
		this.name = name;
		this.email = email;
		this.locale = locale;
		this.description = description;
	}

	@NotNull
	@Column(nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@Column(nullable = false, unique = true)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JoinColumn(name = "LOCALE_ID")
	@ManyToOne
	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	@Column(columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToMany(mappedBy = "emailRecipients")
	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

}
