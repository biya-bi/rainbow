package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.adapters.AuditAdapter;
import org.rainbow.asset.explorer.orm.audit.Auditable;
import org.rainbow.asset.explorer.orm.audit.CurrencyAudit;
import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@EntityListeners(AuditAdapter.class)
@Auditable(audit = CurrencyAudit.class)
public class Currency extends Trackable<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6097591948497015777L;
	private String name;
	private String symbol;
	private String description;
	private List<ProductReceipt> productReceipts;

	public Currency() {
	}

	public Currency(Integer id) {
		super(id);
	}

	public Currency(Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
	}

	public Currency(Date creationDate, Date lastUpdateDate, long version, Integer id) {
		super(creationDate, lastUpdateDate, version, id);
	}

	public Currency(String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate);
	}

	public Currency(String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version,
			Integer id) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
	}

	public Currency(String name, String description, String createdBy, String updatedBy, Date creationDate,
			Date lastUpdateDate) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate);
		this.name = name;
		this.description = description;
	}

	public Currency(String name, String description, String createdBy, String updatedBy, Date creationDate,
			Date lastUpdateDate, long version, Integer id) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
		this.name = name;
		this.description = description;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Override
	public Integer getId() {
		return super.getId();
	}

	@Override
	public void setId(Integer id) {
		super.setId(id);
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	@Column(columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "currency")
	public List<ProductReceipt> getProductReceipts() {
		return productReceipts;
	}

	public void setProductReceipts(List<ProductReceipt> productReceipts) {
		this.productReceipts = productReceipts;
	}

}
