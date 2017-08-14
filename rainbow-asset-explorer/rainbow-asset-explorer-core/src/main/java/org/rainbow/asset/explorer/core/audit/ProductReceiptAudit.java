/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.ProductReceipt;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_RECEIPT_AUDIT")
public class ProductReceiptAudit extends DocumentAudit<ProductReceipt> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6391301310384979341L;
	private Long locationId;
    private Long vendorId;
    private Date receiptDate;
    private Integer currencyId;

    public ProductReceiptAudit() {
    }

    public ProductReceiptAudit(ProductReceipt productReceipt, WriteOperation writeOperation) {
        super(productReceipt, writeOperation);
        this.locationId = productReceipt.getLocation().getId();
        this.vendorId = productReceipt.getVendor().getId();
        this.receiptDate = productReceipt.getReceiptDate();
        this.currencyId = productReceipt.getCurrency().getId();
    }

    @NotNull
    @Column(name = "LOCATION_ID", nullable = false)
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    @NotNull
    @Column(name = "VENDOR_ID", nullable = false)
    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
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
    @Column(name = "CURRENCY_ID", nullable = false)
    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.ProductReceiptAudit[ auditId=" + getAuditId() + " ]";
    }

}
