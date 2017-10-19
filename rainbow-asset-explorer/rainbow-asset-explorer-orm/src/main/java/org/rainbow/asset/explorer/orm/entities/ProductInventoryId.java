package org.rainbow.asset.explorer.orm.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Biya-Bi
 */
@Embeddable
public class ProductInventoryId implements Serializable, Comparable<ProductInventoryId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6382619259488594130L;
	private Long productId;
	private Long locationId;

	public ProductInventoryId() {
	}

	public ProductInventoryId(Long productId, Long locationId) {
		this.productId = productId;
		this.locationId = locationId;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		ProductInventoryId other = (ProductInventoryId) obj;
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
		return this.getClass().getName() + "[ productId=" + getProductId() + ", locationId=" + getLocationId() + " ]";
	}

	@Override
	public int compareTo(ProductInventoryId other) {
		if (other == null) {
			return 1;
		}
		if (this.productId == other.productId) {
			if (this.locationId == other.locationId) {
				return 0;
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

}
