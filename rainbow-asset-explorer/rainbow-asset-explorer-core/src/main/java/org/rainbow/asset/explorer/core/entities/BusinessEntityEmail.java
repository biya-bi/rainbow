/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.BusinessEntityEmailAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_EMAIL", uniqueConstraints = @UniqueConstraint(columnNames = {"BUSINESS_ENTITY_ID", "EMAIL_TYPE"}))
@PrimaryKeyJoinColumn(name = "EMAIL_ID")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = BusinessEntityEmailAudit.class)
public class BusinessEntityEmail extends Email {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3439485901856271007L;
	private BusinessEntity businessEntity;
    private EmailType emailType;

    public BusinessEntityEmail() {
    }

    public BusinessEntityEmail(EmailType emailType) {
        this.emailType = emailType;
    }

    public BusinessEntityEmail(BusinessEntity businessEntity, EmailType emailType, String line, Long id) {
        super(line, id);
        this.businessEntity = businessEntity;
        this.emailType = emailType;
    }

    @NotNull
    @ManyToOne
    @JoinColumn(name = "BUSINESS_ENTITY_ID", nullable = false)
    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }

    @NotNull
    @Column(name = "EMAIL_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType emailType) {
        this.emailType = emailType;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.BusinessEntityEmail[ id=" + getId() + " ]";
    }
}
