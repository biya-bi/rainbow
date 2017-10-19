package org.rainbow.asset.explorer.orm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Biya-Bi
 */
@Embeddable
public class ProductAlertInfoId implements Serializable, Comparable<ProductAlertInfoId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6997096226356836905L;
	private Long productId;
	private Long locationId;
	private AlertCategory alertCategory;

	public ProductAlertInfoId() {
	}

	public ProductAlertInfoId(Long productId, Long locationId, AlertCategory alertCategory) {
		this.productId = productId;
		this.locationId = locationId;
		this.alertCategory = alertCategory;
	}

	@Column(name = "PRODUCT_ID")
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@Column(name = "LOCATION_ID")
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@Column(name = "ALERT_CATEGORY")
	@Enumerated(EnumType.STRING)
	public AlertCategory getAlertCategory() {
		return alertCategory;
	}

	public void setAlertCategory(AlertCategory alertCategory) {
		this.alertCategory = alertCategory;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alertCategory == null) ? 0 : alertCategory.hashCode());
		result = prime * result + ((locationId == null) ? 0 : locationId.hashCode());
		result = prime * result + ((productId == null) ? 0 : productId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductAlertInfoId other = (ProductAlertInfoId) obj;
		if (alertCategory != other.alertCategory)
			return false;
		if (locationId == null) {
			if (other.locationId != null)
				return false;
		} else if (!locationId.equals(other.locationId))
			return false;
		if (productId == null) {
			if (other.productId != null)
				return false;
		} else if (!productId.equals(other.productId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[ productId=" + getProductId() + ", locationId=" + getLocationId()
				+ ", alertCategory=" + getAlertCategory() + " ]";
	}

	@Override
	public int compareTo(ProductAlertInfoId other) {
		if (other == null) {
			return 1;
		}
		if (this.productId == other.productId) {
			if (this.locationId == other.locationId) {
				return compare(this.alertCategory, other.alertCategory);
			} else if (this.locationId == null && other.locationId != null) {
				return -1;
			} else if (this.locationId != null && other.locationId == null) {
				return 1;
			}
			return this.locationId.compareTo(other.locationId);
		}
		if (this.productId == null && other.productId != null) {
			return -1;
		}
		if (this.productId != null && other.productId == null) {
			return 1;
		}
		return this.productId.compareTo(other.productId);
	}

	private int compare(AlertCategory alertCategory1, AlertCategory alertCategory2) {
		if (alertCategory1 == null) {
			if (alertCategory2 == null) {
				return 0;
			}
			return -1;
		} else {
			if (alertCategory2 == null) {
				return 1;
			}
			return alertCategory1.compareTo(alertCategory2);
		}
	}

}
