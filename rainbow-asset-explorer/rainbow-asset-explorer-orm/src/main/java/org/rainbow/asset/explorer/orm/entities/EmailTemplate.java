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

import org.rainbow.asset.explorer.orm.audit.EmailTemplateAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "EMAIL_TEMPLATE")

@Auditable(EmailTemplateAudit.class)
public class EmailTemplate extends AbstractNumericIdAuditableEntity<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7744343302076527121L;
	private String name;
	private String subject;
	private String content;
	private Locale locale;
	private String description;
	private List<Alert> alerts;

	public EmailTemplate() {
	}

	public EmailTemplate(Integer id) {
		super(id);
	}

	public EmailTemplate(String name, String subject, String content, Locale locale) {
		this.name = name;
		this.subject = subject;
		this.content = content;
		this.locale = locale;
	}

	public EmailTemplate(String name, String subject, String content, Locale locale, String createdBy, String updatedBy,
			Date creationDate, Date lastUpdateDate, long version, Integer id) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
		this.name = name;
		this.subject = subject;
		this.content = content;
		this.locale = locale;
	}

	@NotNull
	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@Column(nullable = false)
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Column(nullable = false, columnDefinition = "TEXT")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	@ManyToMany(mappedBy = "emailTemplates")
	public List<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(List<Alert> alerts) {
		this.alerts = alerts;
	}

}
