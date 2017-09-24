package org.rainbow.asset.explorer.orm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ProductIssueDetailId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4099352466646949532L;
	private Long productIssueId;
	private Integer detailId;

	public ProductIssueDetailId() {
	}

	public ProductIssueDetailId(Long purchaseOrderId, Integer detailId) {
		super();
		this.productIssueId = purchaseOrderId;
		this.detailId = detailId;
	}

	@Column(name = "PRODUCT_ISSUE_ID", insertable = false, updatable = false)
	public Long getProductIssueId() {
		return productIssueId;
	}

	public void setProductIssueId(Long userId) {
		this.productIssueId = userId;
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
		result = prime * result + ((productIssueId == null) ? 0 : productIssueId.hashCode());
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
		if (!(obj instanceof ProductIssueDetailId)) {
			return false;
		}
		ProductIssueDetailId other = (ProductIssueDetailId) obj;
		if (detailId == null) {
			if (other.detailId != null) {
				return false;
			}
		} else if (!detailId.equals(other.detailId)) {
			return false;
		}
		if (productIssueId == null) {
			if (other.productIssueId != null) {
				return false;
			}
		} else if (!productIssueId.equals(other.productIssueId)) {
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
		return this.getClass().getName() + "[ productIssueId=" + getProductIssueId() + ", detailId=" + getDetailId()
				+ " ]";
	}

}
