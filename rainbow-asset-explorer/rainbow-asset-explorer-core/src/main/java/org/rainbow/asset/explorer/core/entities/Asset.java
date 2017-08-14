/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import org.rainbow.asset.explorer.core.audit.AssetAudit;
import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Asset.findAll", query = "SELECT a FROM Asset a"),
    @NamedQuery(name = "Asset.findByName", query = "SELECT a FROM Asset a WHERE UPPER(a.name) = :name"),
    @NamedQuery(name = "Asset.findById", query = "SELECT a FROM Asset a WHERE a.id = :id")})
@EntityListeners(AuditAdapter.class)
@Auditable(audit = AssetAudit.class)
public class Asset extends Trackable<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6387357382558725704L;
	private String name;
    private AssetType assetType;
    private ManufacturerBusinessImpact manufacturerBusinessImpact;
    private AssetState state;
    private Vendor vendor;
    private String serialNumber;
    private Double purchaseCost;
    private Currency purchaseCurrency;
    private Date acquisitionDate;
    private Date expiryDate;
    private Date warrantyExpiryDate;
    private DepreciationMethod depreciationMethod;
    private String barCode;
    private Site site;
    private String description;
    private Product product;
    private String tag;

    public Asset() {
    }

    public Asset(Long id) {
        super(id);
    }

    public Asset(String name, String serialNumber, Date creationDate, Date lastUpdateDate) {
        super(creationDate, lastUpdateDate);
        this.name = name;
        this.serialNumber = serialNumber;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Override
    public Long getId() {
        return super.getId();
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @NotNull
    @Column(name = "NAME", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "ASSET_TYPE_ID")
    public AssetType getAssetType() {
        return assetType;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
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

    @ManyToOne
    @JoinColumn(name = "VENDOR_ID")
    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    @NotNull
    @Column(name = "SERIAL_NUMBER", nullable = false, unique = true)
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

    @ManyToOne
    @JoinColumn(name = "PURCHASE_CURRENCY_ID")
    public Currency getPurchaseCurrency() {
        return purchaseCurrency;
    }

    public void setPurchaseCurrency(Currency purchaseCurrency) {
        this.purchaseCurrency = purchaseCurrency;
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

    @ManyToOne
    @JoinColumn(name = "SITE_ID")
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID", nullable = false)
    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.Asset[ id=" + getId() + " ]";
    }
}
