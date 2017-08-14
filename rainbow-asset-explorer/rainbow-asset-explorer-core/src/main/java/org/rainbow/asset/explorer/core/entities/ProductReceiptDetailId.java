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
public class ProductReceiptDetailId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2619216904791253512L;
	private Long productReceiptId;
    private Integer detailId;

    public ProductReceiptDetailId() {
    }

    public ProductReceiptDetailId(Long productReceiptId, Integer detailId) {
        this.productReceiptId = productReceiptId;
        this.detailId = detailId;
    }

    @Column(name = "PRODUCT_RECEIPT_ID")
    public Long getProductReceiptId() {
        return productReceiptId;
    }

    public void setProductReceiptId(Long productReceiptId) {
        this.productReceiptId = productReceiptId;
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
        hash = 37 * hash + Objects.hashCode(this.productReceiptId);
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
        final ProductReceiptDetailId other = (ProductReceiptDetailId) obj;
        if (!Objects.equals(this.productReceiptId, other.productReceiptId)) {
            return false;
        }
        if (!Objects.equals(this.detailId, other.detailId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ProductReceiptDetailId[ productIssueId=" + getProductReceiptId() + ", detailId=" + getDetailId() + " ]";
    }
}
