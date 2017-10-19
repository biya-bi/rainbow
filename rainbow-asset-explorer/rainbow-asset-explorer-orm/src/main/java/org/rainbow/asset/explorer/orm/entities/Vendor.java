package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.audit.VendorAudit;
import org.rainbow.orm.audit.Auditable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "ACCOUNT_NUMBER" }) })

@Auditable(VendorAudit.class)
public class Vendor extends BusinessEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5863825299048264743L;
	private String accountNumber;
	private String name;
	private boolean active;
	private String purchasingUrl;
	private List<ProductReceipt> productReceipts;
	private List<Asset> assets;

	public Vendor() {
	}

	public Vendor(Long id) {
		super(id);
	}

	public Vendor(String accountNumber, String name, boolean active, String purchasingUrl) {
		this.accountNumber = accountNumber;
		this.name = name;
		this.active = active;
		this.purchasingUrl = purchasingUrl;
	}

	public Vendor(String accountNumber, String name, boolean active, String purchasingUrl, Long id) {
		super(id);
		this.accountNumber = accountNumber;
		this.name = name;
		this.active = active;
		this.purchasingUrl = purchasingUrl;
	}

	public Vendor(String accountNumber, String name, boolean active, String purchasingUrl, Date creationDate,
			Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
		this.accountNumber = accountNumber;
		this.name = name;
		this.active = active;
		this.purchasingUrl = purchasingUrl;
	}

	public Vendor(String accountNumber, String name, boolean active, String purchasingUrl, Date creationDate,
			Date lastUpdateDate, long version, Long id) {
		super(creationDate, lastUpdateDate, version, id);
		this.accountNumber = accountNumber;
		this.name = name;
		this.active = active;
		this.purchasingUrl = purchasingUrl;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(name = "ACCOUNT_NUMBER", nullable = false)
	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotNull
	@Column(name = "IS_ACTIVE", nullable = false)
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Column(name = "PURCHASING_URL")
	public String getPurchasingUrl() {
		return purchasingUrl;
	}

	public void setPurchasingUrl(String purchasingUrl) {
		this.purchasingUrl = purchasingUrl;
	}

	@OneToMany(mappedBy = "vendor")
	public List<ProductReceipt> getProductReceipts() {
		return productReceipts;
	}

	public void setProductReceipts(List<ProductReceipt> productReceipts) {
		this.productReceipts = productReceipts;
	}

	@OneToMany(mappedBy = "vendor")
	public List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

}
