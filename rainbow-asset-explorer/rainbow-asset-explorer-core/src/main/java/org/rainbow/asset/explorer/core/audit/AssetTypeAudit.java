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

import org.rainbow.asset.explorer.core.entities.AssetType;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "ASSET_TYPE_AUDIT")
public class AssetTypeAudit extends TrackableAudit<AssetType, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6040039961981576163L;
	private String name;
    private String description;

    public AssetTypeAudit() {
    }

    public AssetTypeAudit(AssetType assetType, WriteOperation writeOperation) {
        super(assetType, writeOperation);
        this.name = assetType.getName();
        this.description = assetType.getDescription();
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
        return "org.rainbow.asset.explorer.core.audit.AssetTypeAudit[ auditId=" + getAuditId() + " ]";
    }

}