/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.ShippingOrderDetailAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SHIPPING_ORDER_DETAIL")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ShippingOrderDetailAudit.class)
public class ShippingOrderDetail extends Trackable<ShippingOrderDetailId> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 688563247967000948L;
	private Product product;
    private Short shippedQuantity;
    private Short receivedQuantity;
    private Short rejectedQuantity;

    public ShippingOrderDetail() {
    }

    @Override
    @EmbeddedId
    public ShippingOrderDetailId getId() {
        return super.getId();
    }

    @Override
    public void setId(ShippingOrderDetailId id) {
        super.setId(id);
    }

    @NotNull
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShippingOrderDetail other = (ShippingOrderDetail) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

}
