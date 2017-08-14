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
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.ShippingOrderAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SHIPPING_ORDER")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ShippingOrderAudit.class)
public class ShippingOrder extends Document {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9163542534164582886L;
	private ShippingOrderStatus status;
    private Location sourceLocation;
    private Location targetLocation;
    private ShipMethod shipMethod;
    private Date shipDate;
    private Date deliveryDate;
    private List<ShippingOrderDetail> details;

    public ShippingOrder() {
    }

    public ShippingOrder(Long id) {
        super(id);
    }

    public ShippingOrder(ShippingOrderStatus status, Location sourceLocation, Location targetLocation, ShipMethod shipMethod, Date shipDate, Date deliveryDate, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate);
        this.status = status;
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
        this.shipMethod = shipMethod;
        this.shipDate = shipDate;
        this.deliveryDate = deliveryDate;
    }

    public ShippingOrder(ShippingOrderStatus status, Location sourceLocation, Location targetLocation, ShipMethod shipMethod, Date shipDate, Date deliveryDate, String referenceNumber, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(referenceNumber, createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
        this.status = status;
        this.sourceLocation = sourceLocation;
        this.targetLocation = targetLocation;
        this.shipMethod = shipMethod;
        this.shipDate = shipDate;
        this.deliveryDate = deliveryDate;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ShippingOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ShippingOrderStatus status) {
        this.status = status;
    }

    @NotNull
    @JoinColumn(name = "SOURCE_LOCATION_ID", nullable = false)
    @ManyToOne
    public Location getSourceLocation() {
        return sourceLocation;
    }

    public void setSourceLocation(Location sourceLocation) {
        this.sourceLocation = sourceLocation;
    }

    @NotNull
    @JoinColumn(name = "TARGET_LOCATION_ID", nullable = false)
    @ManyToOne
    public Location getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Location targetLocation) {
        this.targetLocation = targetLocation;
    }

    @NotNull
    @JoinColumn(name = "SHIP_METHOD_ID", nullable = false)
    @ManyToOne
    public ShipMethod getShipMethod() {
        return shipMethod;
    }

    public void setShipMethod(ShipMethod shipMethod) {
        this.shipMethod = shipMethod;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "SHIP_DATE")
    public Date getShipDate() {
        return shipDate;
    }

    public void setShipDate(Date shipDate) {
        this.shipDate = shipDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DELIVERY_DATE")
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Transient
    public List<ShippingOrderDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ShippingOrderDetail> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ShippingOrder[ id=" + getId() + " ]";
    }
}
