package org.rainbow.asset.explorer.orm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProductReceiptDetailId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4249266075542735618L;
	private Long productReceiptId;
	private Integer detailId;

	public ProductReceiptDetailId() {
	}

	public ProductReceiptDetailId(Long purchaseOrderId, Integer detailId) {
		super();
		this.productReceiptId = purchaseOrderId;
		this.detailId = detailId;
	}

	@Column(name = "PRODUCT_RECEIPT_ID", insertable = false, updatable = false)
	public Long getProductReceiptId() {
		return productReceiptId;
	}

	public void setProductReceiptId(Long userId) {
		this.productReceiptId = userId;
	}

	@Column(name = "DETAIL_ID")
	public Integer getDetailId() {
		return detailId;
	}

	public void setDetailId(Integer loginId) {
		this.detailId = loginId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detailId == null) ? 0 : detailId.hashCode());
		result = prime * result + ((productReceiptId == null) ? 0 : productReceiptId.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ProductReceiptDetailId)) {
			return false;
		}
		ProductReceiptDetailId other = (ProductReceiptDetailId) obj;
		if (detailId == null) {
			if (other.detailId != null) {
				return false;
			}
		} else if (!detailId.equals(other.detailId)) {
			return false;
		}
		if (productReceiptId == null) {
			if (other.productReceiptId != null) {
				return false;
			}
		} else if (!productReceiptId.equals(other.productReceiptId)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + "[ productReceiptId=" + getProductReceiptId() + ", detailId=" + getDetailId()
				+ " ]";
	}

}
