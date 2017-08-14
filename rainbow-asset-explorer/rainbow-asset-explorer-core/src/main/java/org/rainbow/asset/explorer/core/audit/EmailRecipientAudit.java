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

import org.rainbow.asset.explorer.core.entities.EmailRecipient;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "EMAIL_RECIPIENT_AUDIT")
public class EmailRecipientAudit extends TrackableAudit<EmailRecipient, Integer> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3666576408008324217L;
	private String name;
    private String email;
    private Integer localeId;
    private String description;

    public EmailRecipientAudit() {
    }

    public EmailRecipientAudit(EmailRecipient emailRecipient, WriteOperation writeOperation) {
        super(emailRecipient, writeOperation);
        this.name = emailRecipient.getName();
        this.email = emailRecipient.getEmail();
        this.localeId = emailRecipient.getLocale() != null ? emailRecipient.getLocale().getId() : null;
        this.description = emailRecipient.getDescription();
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
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        return "org.rainbow.asset.explorer.core.audit.EmailRecipientAudit[ auditId=" + getAuditId() + " ]";
    }

}
