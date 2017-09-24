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
import org.rainbow.asset.explorer.orm.audit.BusinessEntityPhoneAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_PHONE", uniqueConstraints = @UniqueConstraint(columnNames = { "BUSINESS_ENTITY_ID",
		"PHONE_TYPE" }))
@PrimaryKeyJoinColumn(name = "PHONE_ID")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = BusinessEntityPhoneAudit.class)
public class BusinessEntityPhone extends Phone {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7908632349130101273L;
	private BusinessEntity businessEntity;
	private PhoneType phoneType;

	public BusinessEntityPhone() {
	}

	public BusinessEntityPhone(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

	public BusinessEntityPhone(BusinessEntity businessEntity, PhoneType phoneType, String line, Long id) {
		super(line, id);
		this.businessEntity = businessEntity;
		this.phoneType = phoneType;
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
	@Column(name = "PHONE_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public PhoneType getPhoneType() {
		return phoneType;
	}

	public void setPhoneType(PhoneType phoneType) {
		this.phoneType = phoneType;
	}

}
