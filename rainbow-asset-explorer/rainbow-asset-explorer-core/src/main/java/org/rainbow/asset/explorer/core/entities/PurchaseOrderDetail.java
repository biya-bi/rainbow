/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.PurchaseOrderDetailAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PURCHASE_ORDER_DETAIL")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = PurchaseOrderDetailAudit.class)
public class PurchaseOrderDetail extends Trackable<PurchaseOrderDetailId> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8364995538178938871L;
	private Product product;
    private Date dueDate;
    private Short orderedQuantity;
    private Double unitPrice;
    private Short receivedQuantity;
    private Short rejectedQuantity;

    public PurchaseOrderDetail() {
    }

    @Override
    @EmbeddedId
    public PurchaseOrderDetailId getId() {
        return super.getId();
    }

    @Override
    public void setId(PurchaseOrderDetailId id) {
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
    @Temporal(TemporalType.DATE)
    @Column(name = "DUE_DATE", nullable = false)
    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    @NotNull
    @Min(1)
    @Column(name = "ORDERED_QUANTITY", nullable = false)
    public Short getOrderedQuantity() {
        return orderedQuantity;
    }

    public void setOrderedQuantity(Short orderedQuantity) {
        this.orderedQuantity = orderedQuantity;
    }

    @NotNull
    @Min(0)
    @Column(name = "UNIT_PRICE", nullable = false)
    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
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
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.getId());
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
        final PurchaseOrderDetail other = (PurchaseOrderDetail) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

}
