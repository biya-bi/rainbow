/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.core.entities.Vendor;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "VENDOR_AUDIT")
public class VendorAudit extends TrackableAudit<Vendor, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6745773147395178306L;
	private String accountNumber;
    private String name;
    private boolean active;
    private String purchasingUrl;

    public VendorAudit() {
    }

    public VendorAudit(Vendor vendor, WriteOperation writeOperation) {
        super(vendor, writeOperation);
        this.accountNumber = vendor.getAccountNumber();
        this.name = vendor.getName();
        this.active = vendor.isActive();
        this.purchasingUrl = vendor.getPurchasingUrl();
    }

    @Basic(optional = false)
    @NotNull
    @Size(min = 1)
    @Column(name = "ACCOUNT_NUMBER", nullable = false)
    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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

    @NotNull
    @Column(name = "IS_ACTIVE", nullable = false)
    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Column(name = "PURCHASING_URL")
    public String getPurchasingUrl() {
        return purchasingUrl;
    }

    public void setPurchasingUrl(String purchasingUrl) {
        this.purchasingUrl = purchasingUrl;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.VendorAudit[ auditId=" + getAuditId() + " ]";
    }

}
