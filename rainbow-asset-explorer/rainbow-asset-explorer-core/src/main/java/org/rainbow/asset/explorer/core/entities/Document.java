/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Document extends Trackable<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4461587672420689285L;
	private String referenceNumber;
    private String description;

    public Document() {
    }

    public Document(Long id) {
        super(id);
    }

    public Document(Date creationDate, Date lastUpdateDate) {
        super(creationDate, lastUpdateDate);
    }

    public Document(Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(creationDate, lastUpdateDate, version, id);
    }

    public Document(String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
        super(createdBy, updatedBy, creationDate, lastUpdateDate);
    }

    public Document(String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
    }

    public Document(String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
        super(createdBy, updatedBy, creationDate, lastUpdateDate);
        this.referenceNumber = referenceNumber;
    }

    public Document(String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
        this.referenceNumber = referenceNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
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
        return "org.rainbow.asset.explorer.core.entities.Document[ id=" + getId() + " ]";
    }
}
