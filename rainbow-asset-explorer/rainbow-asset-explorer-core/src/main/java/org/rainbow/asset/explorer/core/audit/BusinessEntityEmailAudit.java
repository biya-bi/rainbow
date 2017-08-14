/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.BusinessEntityEmail;
import org.rainbow.asset.explorer.core.entities.EmailType;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_EMAIL_AUDIT")
public class BusinessEntityEmailAudit extends TrackableAudit<BusinessEntityEmail, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1227714414918775772L;
	private Long businessEntityId;
    private EmailType emailType;
    private String line;

    public BusinessEntityEmailAudit() {
    }

    public BusinessEntityEmailAudit(BusinessEntityEmail businessEntityEmail, WriteOperation writeOperation) {
        super(businessEntityEmail, writeOperation);
        this.businessEntityId = businessEntityEmail.getBusinessEntity().getId();
        this.emailType = businessEntityEmail.getEmailType();
        this.line = businessEntityEmail.getLine();
    }

    @Override
    @Column(name = "EMAIL_ID", nullable = false)
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @NotNull
    @Column(name = "BUSINESS_ENTITY_ID", nullable = false)
    public Long getBusinessEntityId() {
        return businessEntityId;
    }

    public void setBusinessEntityId(Long businessEntityId) {
        this.businessEntityId = businessEntityId;
    }

    @NotNull
    @Column(name = "EMAIL_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public EmailType getEmailType() {
        return emailType;
    }

    public void setEmailType(EmailType addressType) {
        this.emailType = addressType;
    }

    public String getLine() {
        return this.line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.BusinessEntityEmailAudit[ auditId=" + getAuditId() + " ]";
    }

}
