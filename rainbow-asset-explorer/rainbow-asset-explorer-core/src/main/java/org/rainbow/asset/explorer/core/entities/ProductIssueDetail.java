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
import org.rainbow.asset.explorer.core.audit.ProductIssueDetailAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_ISSUE_DETAIL")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ProductIssueDetailAudit.class)
public class ProductIssueDetail extends Trackable<ProductIssueDetailId> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7089407805923712248L;
	private Product product;
    private Short quantity;

    public ProductIssueDetail() {
    }

    @Override
    @EmbeddedId
    public ProductIssueDetailId getId() {
        return super.getId();
    }

    @Override
    public void setId(ProductIssueDetailId id) {
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
    @Column(name = "QUANTITY", nullable = false)
    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.getId());
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
        final ProductIssueDetail other = (ProductIssueDetail) obj;
        if (!Objects.equals(this.getId(), other.getId())) {
            return false;
        }
        return true;
    }

}
