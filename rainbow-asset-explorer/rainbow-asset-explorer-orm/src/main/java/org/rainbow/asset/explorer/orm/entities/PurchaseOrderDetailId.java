package org.rainbow.asset.explorer.orm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PurchaseOrderDetailId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7773448601034323386L;
	private Long purchaseOrderId;
	private Integer detailId;

	public PurchaseOrderDetailId() {
	}

	public PurchaseOrderDetailId(Long purchaseOrderId, Integer detailId) {
		super();
		this.purchaseOrderId = purchaseOrderId;
		this.detailId = detailId;
	}

	@Column(name = "PURCHASE_ORDER_ID", insertable = false, updatable = false)
	public Long getPurchaseOrderId() {
		return purchaseOrderId;
	}

	public void setPurchaseOrderId(Long userId) {
		this.purchaseOrderId = userId;
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
		result = prime * result + ((purchaseOrderId == null) ? 0 : purchaseOrderId.hashCode());
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
		if (!(obj instanceof PurchaseOrderDetailId)) {
			return false;
		}
		PurchaseOrderDetailId other = (PurchaseOrderDetailId) obj;
		if (detailId == null) {
			if (other.detailId != null) {
				return false;
			}
		} else if (!detailId.equals(other.detailId)) {
			return false;
		}
		if (purchaseOrderId == null) {
			if (other.purchaseOrderId != null) {
				return false;
			}
		} else if (!purchaseOrderId.equals(other.purchaseOrderId)) {
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
		return this.getClass().getName() + "[ purchaseOrderId=" + getPurchaseOrderId() + ", detailId=" + getDetailId()
				+ " ]";
	}

}
