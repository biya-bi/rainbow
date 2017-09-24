package org.rainbow.asset.explorer.orm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class ShippingOrderDetailId implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 176939605042273232L;
	private Long shippingOrderId;
	private Integer detailId;

	public ShippingOrderDetailId() {
	}

	public ShippingOrderDetailId(Long purchaseOrderId, Integer detailId) {
		super();
		this.shippingOrderId = purchaseOrderId;
		this.detailId = detailId;
	}

	@Column(name = "SHIPPING_ORDER_ID", insertable = false, updatable = false)
	public Long getShippingOrderId() {
		return shippingOrderId;
	}

	public void setShippingOrderId(Long userId) {
		this.shippingOrderId = userId;
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
		result = prime * result + ((shippingOrderId == null) ? 0 : shippingOrderId.hashCode());
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
		if (!(obj instanceof ShippingOrderDetailId)) {
			return false;
		}
		ShippingOrderDetailId other = (ShippingOrderDetailId) obj;
		if (detailId == null) {
			if (other.detailId != null) {
				return false;
			}
		} else if (!detailId.equals(other.detailId)) {
			return false;
		}
		if (shippingOrderId == null) {
			if (other.shippingOrderId != null) {
				return false;
			}
		} else if (!shippingOrderId.equals(other.shippingOrderId)) {
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
		return this.getClass().getName() + "[ shippingOrderId=" + getShippingOrderId() + ", detailId=" + getDetailId()
				+ " ]";
	}

}
