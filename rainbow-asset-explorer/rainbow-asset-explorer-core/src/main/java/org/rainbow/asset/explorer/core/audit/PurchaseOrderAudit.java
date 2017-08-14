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
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.PurchaseOrder;
import org.rainbow.asset.explorer.core.entities.PurchaseOrderStatus;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PURCHASE_ORDER_AUDIT")
public class PurchaseOrderAudit extends DocumentAudit<PurchaseOrder> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2673930515101236873L;
	private byte revisionNumber;
    private PurchaseOrderStatus status;
    private Long vendorId;
    private Date shipDate;
    private Double taxAmount;
    private Double freight;
    private Long shipMethodId;
    private Long locationId;

    public PurchaseOrderAudit() {
    }

    public PurchaseOrderAudit(PurchaseOrder purchaseOrder, WriteOperation writeOperation) {
        super(purchaseOrder, writeOperation);
        this.revisionNumber = purchaseOrder.getRevisionNumber();
        this.status = purchaseOrder.getStatus();
        this.vendorId = purchaseOrder.getVendor().getId();
        this.shipDate = purchaseOrder.getShipDate();
        this.taxAmount = purchaseOrder.getTaxAmount();
        this.freight = purchaseOrder.getFreight();
        this.shipMethodId = purchaseOrder.getShipMethod() != null ? purchaseOrder.getShipMethod().getId() : null;
        this.locationId = purchaseOrder.getLocation() != null ? purchaseOrder.getLocation().getId() : null;
    }

    @Column(name = "REVISION_NUMBER")
    public byte getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(byte revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public void setStatus(PurchaseOrderStatus status) {
        this.status = status;
    }

    @NotNull
    @Column(name = "VENDOR_ID", nullable = false)
    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "SHIP_DATE")
    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    @NotNull
    @Min(0)
    @Column(name = "TAX_AMOUNT", nullable = false)
    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    @NotNull
    @Min(0)
    @Column(nullable = false)
    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }

    @Column(name = "SHIP_METHOD_ID")
    public Long getShipMethodId() {
        return shipMethodId;
    }

    public void setShipMethodId(Long shipMethod) {
        this.shipMethodId = shipMethod;
    }

    @Column(name = "LOCATION_ID")
    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.PurchaseOrderAudit[ auditId=" + getAuditId() + " ]";
    }
}
