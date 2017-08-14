/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.core.entities.Site;
import org.rainbow.asset.explorer.core.entities.SiteStatus;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SITE_AUDIT")
public class SiteAudit extends TrackableAudit<Site, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6108710634238028354L;
	private String name;
    private String description;
    private String address;
    private String location;
    private SiteStatus status;
    private Date dateOfCommissioning;
    private Date dateOfDecommissioning;

    public SiteAudit() {
    }

    public SiteAudit(Site site, WriteOperation writeOperation) {
        super(site, writeOperation);
        this.name = site.getName();
        this.description = site.getDescription();
        this.address = site.getAddress();
        this.location = site.getLocation();
        this.status = site.getStatus();
        this.dateOfCommissioning = site.getDateOfCommissioning();
        this.dateOfDecommissioning = site.getDateOfDecommissioning();
    }

    @NotNull
    @Size(min = 1)
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public SiteStatus getStatus() {
        return status;
    }

    public void setStatus(SiteStatus status) {
        this.status = status;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_COMMISSIONING")
    public Date getDateOfCommissioning() {
        return dateOfCommissioning;
    }

    public void setDateOfCommissioning(Date dateOfCommissioning) {
        this.dateOfCommissioning = dateOfCommissioning;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_DECOMMISSIONING")
    public Date getDateOfDecommissioning() {
        return dateOfDecommissioning;
    }

    public void setDateOfDecommissioning(Date dateOfDecommissioning) {
        this.dateOfDecommissioning = dateOfDecommissioning;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.SiteAudit[ auditId=" + getAuditId() + " ]";
    }

}