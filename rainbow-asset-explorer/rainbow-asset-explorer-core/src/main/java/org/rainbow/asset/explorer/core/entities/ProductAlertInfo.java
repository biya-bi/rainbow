/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_ALERT_INFO")
public class ProductAlertInfo extends Trackable<ProductAlertInfoId> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5795907501784789868L;
	private AlertType alertType;
    private Short reorderPoint;
    private Short availableQuantity;
    private Date alertDate;

    public ProductAlertInfo() {
    }

    public ProductAlertInfo(ProductAlertInfoId id) {
        super(id);
    }

    @Override
    @EmbeddedId
    public ProductAlertInfoId getId() {
        return super.getId();
    }

    @Override
    public void setId(ProductAlertInfoId id) {
        super.setId(id);
    }

    @Column(name = "ALERT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    @Min(1)
    @NotNull
    @Column(name = "REORDER_POINT", nullable = false)
    public Short getReorderPoint() {
        return reorderPoint;
    }

    public void setReorderPoint(Short reorderPoint) {
        this.reorderPoint = reorderPoint;
    }

    @Min(1)
    @NotNull
    @Column(name = "AVAILABLE_QUANTITY", nullable = false)
    public Short getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Short availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ALERT_DATE", nullable = false)
    public Date getAlertDate() {
        return alertDate;
    }

    public void setAlertDate(Date alertDate) {
        this.alertDate = alertDate;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.ProductAlertInfo[ id=" + getId() + " ]";
    }
}
