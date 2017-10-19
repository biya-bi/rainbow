package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.entities.AddressType;
import org.rainbow.asset.explorer.orm.entities.BusinessEntityAddress;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_ADDRESS_AUDIT")
public class BusinessEntityAddressAudit extends AbstractAuditableEntityAudit<BusinessEntityAddress, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 651635635143387299L;
	private Long businessEntityId;
	private AddressType addressType;
	private String line1;
	private String line2;

	public BusinessEntityAddressAudit() {
	}

	public BusinessEntityAddressAudit(BusinessEntityAddress businessEntityAddress, WriteOperation writeOperation) {
		super(businessEntityAddress, writeOperation);
		this.businessEntityId = businessEntityAddress.getBusinessEntity().getId();
		this.addressType = businessEntityAddress.getAddressType();
		this.line1 = businessEntityAddress.getLine1();
		this.line2 = businessEntityAddress.getLine2();
	}

	@Override
	@Column(name = "ADDRESS_ID", nullable = false)
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	@NotNull
	@Column(name = "BUSINESS_ENTITY_ID", nullable = false)
	public Long getBusinessEntityId() {
		return businessEntityId;
	}

	public void setBusinessEntityId(Long businessEntityId) {
		this.businessEntityId = businessEntityId;
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

	public String getLine1() {
		return this.line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return this.line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

}
