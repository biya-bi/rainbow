/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 *
 * @author Biya-Bi
 */
@Embeddable
public class ProductAlertInfoId implements Serializable {

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
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this.productId);
        hash = 31 * hash + Objects.hashCode(this.locationId);
        hash = 31 * hash + Objects.hashCode(this.alertCategory);
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
        final ProductAlertInfoId other = (ProductAlertInfoId) obj;
        if (!Objects.equals(this.productId, other.productId)) {
            return false;
        }
        if (!Objects.equals(this.locationId, other.locationId)) {
            return false;
        }
        if (this.alertCategory != other.alertCategory) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ProductAlertInfoId[ productId=" + getProductId() + ", locationId=" + getLocationId() + ", alertCategory=" + getAlertCategory() + " ]";
    }
}
