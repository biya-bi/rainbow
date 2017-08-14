/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.core.entities.Document;

/**
 *
 * @author Biya-Bi
 * @param <T>
 */
@MappedSuperclass
public abstract class DocumentAudit<T extends Document> extends TrackableAudit<T, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2921408246354629915L;
	private String referenceNumber;
    private String description;

    public DocumentAudit() {
    }

    public DocumentAudit(T document, WriteOperation writeOperation) {
        super(document, writeOperation);
        this.referenceNumber = document.getReferenceNumber();
        this.description = document.getDescription();
    }

    @Basic(optional = false)
    @NotNull
    @Size(min = 1)
    @Column(name = "REFERENCE_NUMBER", nullable = false)
    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
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
        return "org.rainbow.asset.explorer.core.audit.DocumentAudit[ auditId=" + getAuditId() + " ]";
    }

}
