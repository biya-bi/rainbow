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
@Table(name = "PRODUCT_ISSUE_DETAIL_AUDIT")
public class ProductIssueDetailAudit extends TrackableAudit<ProductIssueDetail, ProductIssueDetailId> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1649013407527189874L;
	private Long productId;
    private Short quantity;

    public ProductIssueDetailAudit() {
    }

    public ProductIssueDetailAudit(ProductIssueDetail productIssueDetail, WriteOperation writeOperation) {
        super(productIssueDetail, writeOperation);
        this.productId = productIssueDetail.getProduct().getId();
        this.quantity = productIssueDetail.getQuantity();
    }

    @Override
    @Embedded
    public ProductIssueDetailId getId() {
        return super.getId();
    }

    @Override
    public void setId(ProductIssueDetailId id) {
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
    @Column(name = "QUANTITY", nullable = false)
    public Short getQuantity() {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.ProductIssueDetailAudit[ auditId=" + getAuditId() + " ]";
    }
}
