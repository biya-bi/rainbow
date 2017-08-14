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
import org.rainbow.asset.explorer.core.audit.ProductReceiptAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_RECEIPT")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ProductReceiptAudit.class)
public class ProductReceipt extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5847508064723426307L;
	private List<ProductReceiptDetail> details;
    private Location location;
    private Vendor vendor;
    private Date receiptDate;
    private Currency currency;

    public ProductReceipt() {
    }

    public ProductReceipt(Long id) {
        super(id);
    }

    public ProductReceipt(Location location, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate);
        this.location = location;
    }

    public ProductReceipt(Location location, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
        this.location = location;
    }

    @Transient
    public List<ProductReceiptDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ProductReceiptDetail> details) {
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

    @NotNull
    @JoinColumn(name = "VENDOR_ID", nullable = false)
    @ManyToOne
    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "RECEIPT_DATE", nullable = false)
    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    @NotNull
    @JoinColumn(name = "CURRENCY_ID", nullable = false)
    @ManyToOne
    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Transient
    public double getTotalPrice() {
        if (details == null) {
            return 0;
        }
        double total = 0;
        for (ProductReceiptDetail detail : details) {
            total += detail.getSubTotal();
        }
        return total;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ProductReceipt[ id=" + getId() + " ]";
    }
}
