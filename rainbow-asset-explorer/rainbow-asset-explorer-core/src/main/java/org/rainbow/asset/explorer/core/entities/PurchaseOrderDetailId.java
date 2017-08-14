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
public class PurchaseOrderDetailId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 838371687518601041L;
	private Long purchaseOrderId;
    private Integer detailId;

    public PurchaseOrderDetailId() {
    }

    public PurchaseOrderDetailId(Long purchaseOrderId, Integer detailId) {
        this.purchaseOrderId = purchaseOrderId;
        this.detailId = detailId;
    }

    @Column(name = "PURCHASE_ORDER_ID")
    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
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
        int hash = 5;
        hash = 79 * hash + Objects.hashCode(this.purchaseOrderId);
        hash = 79 * hash + Objects.hashCode(this.detailId);
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
        final PurchaseOrderDetailId other = (PurchaseOrderDetailId) obj;
        if (!Objects.equals(this.purchaseOrderId, other.purchaseOrderId)) {
            return false;
        }
        if (!Objects.equals(this.detailId, other.detailId)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.PurchaseOrderDetailId[ purchaseOrderId=" + getPurchaseOrderId() + ", detailId=" + getDetailId() + " ]";
    }
}
