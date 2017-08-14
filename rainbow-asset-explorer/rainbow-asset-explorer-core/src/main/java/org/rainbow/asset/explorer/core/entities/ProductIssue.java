/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.ProductIssueAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_ISSUE")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ProductIssueAudit.class)
public class ProductIssue extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3284558623433232003L;
	private List<ProductIssueDetail> details;
    private Location location;
    private Department department;
    private String requisitioner;
    private Date issueDate;

    public ProductIssue() {
    }

    public ProductIssue(Long id) {
        super(id);
    }

    public ProductIssue(Location location, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate);
        this.location = location;
    }

    public ProductIssue(Location location, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
        this.location = location;
    }

    @Transient
    public List<ProductIssueDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ProductIssueDetail> details) {
        this.details = details;
    }

    @NotNull
    @JoinColumn(name = "LOCATION_ID", nullable = false)
    @ManyToOne
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @ManyToOne
    @JoinColumn(name = "DEPARTMENT_ID")
    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Column(nullable = false)
    public String getRequisitioner() {
        return requisitioner;
    }

    public void setRequisitioner(String requisitioner) {
        this.requisitioner = requisitioner;
    }

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "ISSUE_DATE", nullable = false)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ProductIssue[ id=" + getId() + " ]";
    }
}
