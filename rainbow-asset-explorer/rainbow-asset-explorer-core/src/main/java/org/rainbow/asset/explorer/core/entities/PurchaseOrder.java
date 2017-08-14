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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.PurchaseOrderAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PURCHASE_ORDER")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = PurchaseOrderAudit.class)
public class PurchaseOrder extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2905529736192505911L;
	private byte revisionNumber;
    private PurchaseOrderStatus status;
    private Vendor vendor;
    private Date shipDate;
    private Double taxAmount;
    private Double freight;
    private List<PurchaseOrderDetail> details;
    private ShipMethod shipMethod;
    private Location location;

    public PurchaseOrder() {
    }

    public PurchaseOrder(Long id) {
        super(id);
    }

    public PurchaseOrder(byte revisionNumber, PurchaseOrderStatus status, Vendor vendor, Date shipDate, Double taxAmount, Double freight, ShipMethod shipMethod, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate);
        this.revisionNumber = revisionNumber;
        this.status = status;
        this.vendor = vendor;
        this.shipDate = shipDate;
        this.taxAmount = taxAmount;
        this.freight = freight;
        this.shipMethod = shipMethod;
    }

    public PurchaseOrder(byte revisionNumber, PurchaseOrderStatus status, Vendor vendor, Date shipDate, Double taxAmount, Double freight, ShipMethod shipMethod, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
        this.revisionNumber = revisionNumber;
        this.status = status;
        this.vendor = vendor;
        this.shipDate = shipDate;
        this.taxAmount = taxAmount;
        this.freight = freight;
        this.shipMethod = shipMethod;
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
    @ManyToOne
    @JoinColumn(name = "VENDOR_ID", nullable = false)
    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
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

    @Transient
    public List<PurchaseOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<PurchaseOrderDetail> details) {
        this.details = details;
    }

    @JoinColumn(name = "SHIP_METHOD_ID")
    @ManyToOne
    public ShipMethod getShipMethod() {
        return shipMethod;
    }

    public void setShipMethod(ShipMethod shipMethod) {
        this.shipMethod = shipMethod;
    }

    @JoinColumn(name = "LOCATION_ID")
    @ManyToOne
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.PurchaseOrder[ id=" + getId() + " ]";
    }
}
