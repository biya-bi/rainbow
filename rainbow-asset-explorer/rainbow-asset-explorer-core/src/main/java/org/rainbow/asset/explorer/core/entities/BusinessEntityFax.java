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
import org.rainbow.asset.explorer.core.audit.BusinessEntityFaxAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_FAX", uniqueConstraints = @UniqueConstraint(columnNames = {"BUSINESS_ENTITY_ID", "FAX_TYPE"}))
@PrimaryKeyJoinColumn(name = "FAX_ID")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = BusinessEntityFaxAudit.class)
public class BusinessEntityFax extends Fax {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2576748446544252661L;
	private BusinessEntity businessEntity;
    private FaxType faxType;

    public BusinessEntityFax() {
    }

    public BusinessEntityFax(FaxType faxType) {
        this.faxType = faxType;
    }

    public BusinessEntityFax(BusinessEntity businessEntity, FaxType faxType, String line, Long id) {
        super(line, id);
        this.businessEntity = businessEntity;
        this.faxType = faxType;
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
    @Column(name = "FAX_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public FaxType getFaxType() {
        return faxType;
    }

    public void setFaxType(FaxType faxType) {
        this.faxType = faxType;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.BusinessEntityFax[ id=" + getId() + " ]";
    }
}