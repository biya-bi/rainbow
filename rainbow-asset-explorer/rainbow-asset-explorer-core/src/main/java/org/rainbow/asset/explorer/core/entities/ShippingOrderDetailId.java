/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Biya-Bi
 */
@Embeddable
public class ShippingOrderDetailId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7118214173268862114L;
	private Long shippingOrderId;
    private Integer detailId;

    public ShippingOrderDetailId() {
    }

    public ShippingOrderDetailId(Long shippingOrderId, Integer detailId) {
        this.shippingOrderId = shippingOrderId;
        this.detailId = detailId;
    }

    @Column(name = "SHIPPING_ORDER_ID")
    public Long getShippingOrderId() {
        return shippingOrderId;
    }

    public void setShippingOrderId(Long shippingOrderId) {
        this.shippingOrderId = shippingOrderId;
    }

    @Column(name = "DETAIL_ID")
    public Integer getDetailId() {
        return detailId;
    }

    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.shippingOrderId);
        hash = 89 * hash + Objects.hashCode(this.detailId);
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
        final ShippingOrderDetailId other = (ShippingOrderDetailId) obj;
        if (!Objects.equals(this.shippingOrderId, other.shippingOrderId)) {
            return false;
        }
        if (!Objects.equals(this.detailId, other.detailId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ShippingOrderDetailId[ shippingOrderId=" + getShippingOrderId() + ", detailId=" + getDetailId() + " ]";
    }
}
