package org.rainbow.asset.explorer.orm.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Biya-Bi
 */
@Embeddable
public class ProductInventoryId implements Serializable {

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
		int hash = 3;
		hash = 37 * hash + Objects.hashCode(this.productId);
		hash = 37 * hash + Objects.hashCode(this.locationId);
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
		final ProductInventoryId other = (ProductInventoryId) obj;
		if (!Objects.equals(this.productId, other.productId)) {
			return false;
		}
		if (!Objects.equals(this.locationId, other.locationId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[ productId=" + getProductId() + ", locationId=" + getLocationId() + " ]";
	}
}
