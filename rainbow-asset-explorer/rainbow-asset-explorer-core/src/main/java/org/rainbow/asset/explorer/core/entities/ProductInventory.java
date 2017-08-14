/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_INVENTORY")
@XmlRootElement
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

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ProductInventory[ id=" + getId() + " ]";
    }
}
