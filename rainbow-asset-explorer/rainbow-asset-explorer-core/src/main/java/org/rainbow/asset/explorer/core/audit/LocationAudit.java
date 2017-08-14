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
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.core.entities.Location;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "LOCATION_AUDIT")
public class LocationAudit extends TrackableAudit<Location, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3273084015560219910L;
	private String name;
    private String description;

    public LocationAudit() {
    }

    public LocationAudit(Location location, WriteOperation writeOperation) {
        super(location, writeOperation);
        this.name = location.getName();
        this.description = location.getDescription();
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

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.LocationAudit[ auditId=" + getAuditId() + " ]";
    }

}
