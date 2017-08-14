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
public class ProductIssueDetailId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1199636583791613950L;
	private Long productIssueId;
    private Integer detailId;

    public ProductIssueDetailId() {
    }

    public ProductIssueDetailId(Long productIssueId, Integer detailId) {
        this.productIssueId = productIssueId;
        this.detailId = detailId;
    }

    @Column(name = "PRODUCT_ISSUE_ID")
    public Long getProductIssueId() {
        return productIssueId;
    }

    public void setProductIssueId(Long productIssueId) {
        this.productIssueId = productIssueId;
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
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.productIssueId);
        hash = 37 * hash + Objects.hashCode(this.detailId);
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
        final ProductIssueDetailId other = (ProductIssueDetailId) obj;
        if (!Objects.equals(this.productIssueId, other.productIssueId)) {
            return false;
        }
        if (!Objects.equals(this.detailId, other.detailId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ProductIssueDetailId[ productIssueId=" + getProductIssueId() + ", detailId=" + getDetailId() + " ]";
    }
}
