package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.entities.BusinessEntityPhone;
import org.rainbow.asset.explorer.orm.entities.PhoneType;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_PHONE_AUDIT")
public class BusinessEntityPhoneAudit extends TrackableAudit<BusinessEntityPhone, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -148993029716242424L;
	private Long businessEntityId;
	private PhoneType phoneType;
	private String line;

	public BusinessEntityPhoneAudit() {
	}

	public BusinessEntityPhoneAudit(BusinessEntityPhone businessEntityPhone, WriteOperation writeOperation) {
		super(businessEntityPhone, writeOperation);
		this.businessEntityId = businessEntityPhone.getBusinessEntity().getId();
		this.phoneType = businessEntityPhone.getPhoneType();
		this.line = businessEntityPhone.getLine();
	}

	@Override
	@Column(name = "PHONE_ID", nullable = false)
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
	@Column(name = "PHONE_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public PhoneType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(PhoneType addressType) {
		this.phoneType = addressType;
	}

	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	@Override
	public String toString() {
		return "org.rainbow.asset.explorer.core.audit.BusinessEntityPhoneAudit[ auditId=" + getAuditId() + " ]";
	}

}
