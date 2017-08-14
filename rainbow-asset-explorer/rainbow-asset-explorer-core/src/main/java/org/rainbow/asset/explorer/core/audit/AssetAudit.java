/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.Asset;
import org.rainbow.asset.explorer.core.entities.AssetState;
import org.rainbow.asset.explorer.core.entities.DepreciationMethod;
import org.rainbow.asset.explorer.core.entities.ManufacturerBusinessImpact;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "ASSET_AUDIT")
public class AssetAudit extends TrackableAudit<Asset, Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1977143906601852021L;
	private String name;
    private Long assetTypeId;
    private ManufacturerBusinessImpact manufacturerBusinessImpact;
    private AssetState state;
    private Long vendorId;
    private String serialNumber;
    private Double purchaseCost;
    private Integer purchaseCurrencyId;
    private Date acquisitionDate;
    private Date expiryDate;
    private Date warrantyExpiryDate;
    private DepreciationMethod depreciationMethod;
    private String barCode;
    private Long siteId;
    private String description;
    private Long productId;
    private String tag;

    public AssetAudit() {
    }

    public AssetAudit(Asset asset, WriteOperation writeOperation) {
        super(asset, writeOperation);
        this.name = asset.getName();
        if (asset.getAssetType() != null) {
            this.assetTypeId = asset.getAssetType().getId();
        }
        this.manufacturerBusinessImpact = asset.getManufacturerBusinessImpact();
        this.state = asset.getState();
        if (asset.getVendor() != null) {
            this.vendorId = asset.getVendor().getId();
        }
        this.serialNumber = asset.getSerialNumber();
        this.purchaseCost = asset.getPurchaseCost();
        if (asset.getPurchaseCurrency() != null) {
            this.purchaseCurrencyId = asset.getPurchaseCurrency().getId();
        }
        this.acquisitionDate = asset.getAcquisitionDate();
        this.expiryDate = asset.getExpiryDate();
        this.warrantyExpiryDate = asset.getWarrantyExpiryDate();
        this.depreciationMethod = asset.getDepreciationMethod();
        this.barCode = asset.getBarCode();
        if (asset.getSite() != null) {
            this.siteId = asset.getSite().getId();
        }
        this.description = asset.getDescription();
        this.productId = asset.getProduct().getId();
        this.tag = asset.getTag();
    }

    @NotNull
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "ASSET_TYPE_ID")
    public Long getAssetTypeId() {
        return assetTypeId;
    }

    public void setAssetTypeId(Long assetTypeId) {
        this.assetTypeId = assetTypeId;
    }

    @Column(name = "MANUFACTURER_BUSINESS_IMPACT")
    @Enumerated(EnumType.STRING)
    public ManufacturerBusinessImpact getManufacturerBusinessImpact() {
        return manufacturerBusinessImpact;
    }

    public void setManufacturerBusinessImpact(ManufacturerBusinessImpact manufacturerBusinessImpact) {
        this.manufacturerBusinessImpact = manufacturerBusinessImpact;
    }

    @Column(name = "ASSET_STATE")
    @Enumerated(EnumType.STRING)
    public AssetState getState() {
        return state;
    }

    public void setState(AssetState state) {
        this.state = state;
    }

    @Column(name = "VENDOR_ID")
    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    @NotNull
    @Column(name = "SERIAL_NUMBER", nullable = false)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Min(0)
    @Column(name = "PURCHASE_COST")
    public Double getPurchaseCost() {
        return purchaseCost;
    }

    public void setPurchaseCost(Double purchaseCost) {
        this.purchaseCost = purchaseCost;
    }

    @Column(name = "PURCHASE_CURRENCY_ID")
    public Integer getPurchaseCurrencyId() {
        return purchaseCurrencyId;
    }

    public void setPurchaseCurrencyId(Integer purchaseCurrencyId) {
        this.purchaseCurrencyId = purchaseCurrencyId;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "ACQUISITION_DATE")
    public Date getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(Date acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "EXPIRY_DATE")
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "WARRANTY_EXPIRY_DATE")
    public Date getWarrantyExpiryDate() {
        return warrantyExpiryDate;
    }

    public void setWarrantyExpiryDate(Date warrantyExpiryDate) {
        this.warrantyExpiryDate = warrantyExpiryDate;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "DEPRECIATION_METHOD")
    public DepreciationMethod getDepreciationMethod() {
        return depreciationMethod;
    }

    public void setDepreciationMethod(DepreciationMethod depreciationMethod) {
        this.depreciationMethod = depreciationMethod;
    }

    @Column(name = "BAR_CODE")
    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    @Column(name = "SITE_ID")
    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @Column(name = "PRODUCT_ID", nullable = false)
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.AssetAudit[ auditId=" + getAuditId() + " ]";
    }

}
