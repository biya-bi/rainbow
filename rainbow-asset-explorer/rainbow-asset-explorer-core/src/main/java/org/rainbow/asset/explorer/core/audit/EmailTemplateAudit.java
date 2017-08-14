/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.EmailTemplate;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "EMAIL_TEMPLATE_AUDIT")
public class EmailTemplateAudit extends TrackableAudit<EmailTemplate, Integer> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3392453059220696884L;
	private String name;
    private String subject;
    private String content;
    private Integer localeId;
    private String description;

    public EmailTemplateAudit() {
    }

    public EmailTemplateAudit(EmailTemplate emailTemplate, WriteOperation writeOperation) {
        super(emailTemplate, writeOperation);
        this.name = emailTemplate.getName();
        this.subject = emailTemplate.getSubject();
        this.content = emailTemplate.getContent();
        this.localeId = emailTemplate.getLocale() != null ? emailTemplate.getLocale().getId() : null;
        this.description = emailTemplate.getDescription();
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

    @Column(name = "LOCALE_ID")
    public Integer getLocaleId() {
        return localeId;
    }

    public void setLocaleId(Integer localeId) {
        this.localeId = localeId;
    }

    @Column(columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.EmailTemplateAudit[ auditId=" + getAuditId() + " ]";
    }
}
