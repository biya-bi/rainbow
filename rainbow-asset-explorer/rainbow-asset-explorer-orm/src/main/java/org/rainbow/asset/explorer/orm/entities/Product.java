package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.rainbow.asset.explorer.orm.audit.ProductAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Product.findAll", query = "SELECT p FROM Product p"),
		@NamedQuery(name = "Product.findByNumber", query = "SELECT p FROM Product p WHERE UPPER(p.number) = :number"),
		@NamedQuery(name = "Product.findByName", query = "SELECT p FROM Product p WHERE UPPER(p.name) = :name"),
		@NamedQuery(name = "Product.findById", query = "SELECT p FROM Product p WHERE p.id = :id") })

@Auditable(ProductAudit.class)
public class Product extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8709176576650623349L;
	private String number;
	private String name;
	private Short safetyStockLevel;
	private Short reorderPoint;
	private Short stockCover;
	private Date criticalStockDate;
	private String description;
	private Manufacturer manufacturer;
	private List<Asset> assets;

	public Product() {
	}

	public Product(Long id) {
		super(id);
	}

	public Product(String number, String name, Short safetyStockLevel, Short reorderPoint, Short stockCover,
			String description, Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
		this.number = number;
		this.name = name;
		this.safetyStockLevel = safetyStockLevel;
		this.reorderPoint = reorderPoint;
		this.stockCover = stockCover;
		this.description = description;
	}

	public Product(String number, String name, Short safetyStockLevel, Short reorderPoint, Short stockCover,
			String description, Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creationDate, lastUpdateDate, version, id);
		this.number = number;
		this.name = name;
		this.safetyStockLevel = safetyStockLevel;
		this.reorderPoint = reorderPoint;
		this.stockCover = stockCover;
		this.description = description;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(name = "NUMBER", nullable = false, unique = true)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(name = "NAME", nullable = false, unique = true)
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

	@ManyToOne
	@JoinColumn(name = "MANUFACTURER_ID")
	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	@OneToMany(mappedBy = "product")
	public List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

}
