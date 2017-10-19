package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.entities.AbstractAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_INVENTORY")
public class ProductInventory extends AbstractAuditableEntity<ProductInventoryId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1756755603720578535L;
	private Short quantity;
	private Product product;
	private Location location;

	@Override
	@EmbeddedId
	public ProductInventoryId getId() {
		return super.getId();
	}

	@Override
	public void setId(ProductInventoryId id) {
		super.setId(id);
	}

	@Min(0)
	@NotNull
	@Column(name = "QUANTITY", nullable = false)
	public Short getQuantity() {
		return quantity;
	}

	public void setQuantity(Short quantity) {
		this.quantity = quantity;
	}

	@ManyToOne
	@JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@ManyToOne
	@JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
