package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.adapters.AuditAdapter;
import org.rainbow.asset.explorer.orm.audit.Auditable;
import org.rainbow.asset.explorer.orm.audit.BusinessEntityAddressAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_ADDRESS", uniqueConstraints = @UniqueConstraint(columnNames = { "BUSINESS_ENTITY_ID",
		"ADDRESS_TYPE" }))
@PrimaryKeyJoinColumn(name = "ADDRESS_ID")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = BusinessEntityAddressAudit.class)
public class BusinessEntityAddress extends Address {

	/**
	 * 
	 */
	private static final long serialVersionUID = 624059733198527460L;
	private BusinessEntity businessEntity;
	private AddressType addressType;

	public BusinessEntityAddress() {
	}

	public BusinessEntityAddress(AddressType addressType) {
		this.addressType = addressType;
	}

	public BusinessEntityAddress(BusinessEntity businessEntity, AddressType addressType, String line1, String line2,
			Long id) {
		super(line1, line2, id);
		this.businessEntity = businessEntity;
		this.addressType = addressType;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "BUSINESS_ENTITY_ID", nullable = false)
	public BusinessEntity getBusinessEntity() {
		return businessEntity;
	}

	public void setBusinessEntity(BusinessEntity businessEntity) {
		this.businessEntity = businessEntity;
	}

	@NotNull
	@Column(name = "ADDRESS_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public AddressType getAddressType() {
		return addressType;
	}

	public void setAddressType(AddressType addressType) {
		this.addressType = addressType;
	}

}
