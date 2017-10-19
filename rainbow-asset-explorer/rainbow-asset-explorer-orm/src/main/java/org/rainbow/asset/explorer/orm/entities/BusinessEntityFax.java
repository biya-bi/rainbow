package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.audit.BusinessEntityFaxAudit;
import org.rainbow.orm.audit.Auditable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY_FAX", uniqueConstraints = @UniqueConstraint(columnNames = { "BUSINESS_ENTITY_ID",
		"FAX_TYPE" }))
@PrimaryKeyJoinColumn(name = "FAX_ID")

@Auditable(BusinessEntityFaxAudit.class)
public class BusinessEntityFax extends Fax {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2576748446544252661L;
	private BusinessEntity businessEntity;
	private FaxType faxType;

	public BusinessEntityFax() {
	}

	public BusinessEntityFax(FaxType faxType) {
		this.faxType = faxType;
	}

	public BusinessEntityFax(BusinessEntity businessEntity, FaxType faxType, String line, Long id) {
		super(line, id);
		this.businessEntity = businessEntity;
		this.faxType = faxType;
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
	@Column(name = "FAX_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public FaxType getFaxType() {
		return faxType;
	}

	public void setFaxType(FaxType faxType) {
		this.faxType = faxType;
	}

}
