/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.core.entities.Product;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_AUDIT")
public class ProductAudit extends TrackableAudit<Product, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 740345376873519838L;
	private String number;
    private String name;
    private Short safetyStockLevel;
    private Short reorderPoint;
    private Short stockCover;
    private Date criticalStockDate;
    private String description;
    private Long manufacturerId;

    public ProductAudit() {
    }

    public ProductAudit(Product product, WriteOperation writeOperation) {
        super(product, writeOperation);
        this.number = product.getNumber();
        this.name = product.getName();
        this.safetyStockLevel = product.getSafetyStockLevel();
        this.reorderPoint = product.getReorderPoint();
        this.stockCover = product.getStockCover();
        this.description = product.getDescription();
        if (product.getManufacturer() != null) {
            this.manufacturerId = product.getManufacturer().getId();
        }
    }

    @NotNull
    @Size(min = 1)
    @Column(name = "NUMBER", nullable = false)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @NotNull
    @Size(min = 1)
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "SAFETY_STOCK_LEVEL")
    public Short getSafetyStockLevel() {
        return safetyStockLevel;
    }

    public void setSafetyStockLevel(Short safetyStockLevel) {
        this.safetyStockLevel = safetyStockLevel;
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

    @Min(0)
    @Column(name = "STOCK_COVER")
    public Short getStockCover() {
        return stockCover;
    }

    public void setStockCover(Short stockCover) {
        this.stockCover = stockCover;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "CRITICAL_STOCK_DATE")
    public Date getCriticalStockDate() {
        return criticalStockDate;
    }

    public void setCriticalStockDate(Date criticalStockDate) {
        this.criticalStockDate = criticalStockDate;
    }

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "MANUFACTURER_ID")
    public Long getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(Long manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.ProductAudit[ auditId=" + getAuditId() + " ]";
    }

}
