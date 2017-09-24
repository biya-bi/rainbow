package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_INVENTORY")
public class ProductInventory extends Trackable<ProductInventoryId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1756755603720578535L;
	private Short quantity;

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

}
