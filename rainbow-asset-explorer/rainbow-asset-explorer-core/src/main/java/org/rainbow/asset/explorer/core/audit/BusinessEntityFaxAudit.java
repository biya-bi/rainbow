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

import org.rainbow.asset.explorer.core.entities.BusinessEntityFax;
import org.rainbow.asset.explorer.core.entities.FaxType;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_FAX_AUDIT")
public class BusinessEntityFaxAudit extends TrackableAudit<BusinessEntityFax, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7943991817198305038L;
	private Long businessEntityId;
    private FaxType faxType;
    private String line;

    public BusinessEntityFaxAudit() {
    }

    public BusinessEntityFaxAudit(BusinessEntityFax businessEntityFax, WriteOperation writeOperation) {
        super(businessEntityFax, writeOperation);
        this.businessEntityId = businessEntityFax.getBusinessEntity().getId();
        this.faxType = businessEntityFax.getFaxType();
        this.line = businessEntityFax.getLine();
    }

    @Override
    @Column(name = "FAX_ID", nullable = false)
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
    @Column(name = "FAX_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public FaxType getFaxType() {
        return faxType;
    }

    public void setFaxType(FaxType addressType) {
        this.faxType = addressType;
    }

    public String getLine() {
        return this.line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.BusinessEntityFaxAudit[ auditId=" + getAuditId() + " ]";
    }

}
