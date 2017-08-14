/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.*;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SHIPPING_ORDER_DETAIL_AUDIT")
public class ShippingOrderDetailAudit extends TrackableAudit<ShippingOrderDetail, ShippingOrderDetailId> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3675413710142041218L;
	private Long productId;
    private Short shippedQuantity;
    private Short receivedQuantity;
    private Short rejectedQuantity;

    public ShippingOrderDetailAudit() {
    }

    public ShippingOrderDetailAudit(ShippingOrderDetail shippingOrderDetail, WriteOperation writeOperation) {
        super(shippingOrderDetail, writeOperation);
        this.productId = shippingOrderDetail.getProduct().getId();
        this.shippedQuantity = shippingOrderDetail.getShippedQuantity();
        this.receivedQuantity = shippingOrderDetail.getReceivedQuantity();
        this.rejectedQuantity = shippingOrderDetail.getRejectedQuantity();
    }

    @Override
    @Embedded
    public ShippingOrderDetailId getId() {
        return super.getId();
    }

    @Override
    public void setId(ShippingOrderDetailId id) {
        super.setId(id);
    }

    @NotNull
    @Column(name = "PRODUCT_ID", nullable = false)
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    @NotNull
    @Min(1)
    @Column(name = "SHIPPED_QUANTITY", nullable = false)
    public Short getShippedQuantity() {
        return shippedQuantity;
    }

    public void setShippedQuantity(Short shippedQuantity) {
        this.shippedQuantity = shippedQuantity;
    }

    @NotNull
    @Min(0)
    @Column(name = "RECEIVED_QUANTITY", nullable = false)
    public Short getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(Short receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    @NotNull
    @Min(0)
    @Column(name = "REJECTED_QUANTITY", nullable = false)
    public Short getRejectedQuantity() {
        return rejectedQuantity;
    }

    public void setRejectedQuantity(Short rejectedQuantity) {
        this.rejectedQuantity = rejectedQuantity;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.ShippingOrderDetailAudit[ auditId=" + getAuditId() + " ]";
    }
}
