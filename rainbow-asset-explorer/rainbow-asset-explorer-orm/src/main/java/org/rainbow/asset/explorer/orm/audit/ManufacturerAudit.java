package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.entities.Manufacturer;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "MANUFACTURER_AUDIT")
public class ManufacturerAudit extends AbstractAuditableEntityAudit<Manufacturer, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4619973700697918526L;
	private String name;
	private String description;

	public ManufacturerAudit() {
	}

	public ManufacturerAudit(Manufacturer manufacturer, WriteOperation writeOperation) {
		super(manufacturer, writeOperation);
		this.name = manufacturer.getName();
		this.description = manufacturer.getDescription();
	}

	@NotNull
	@Size(min = 1)
	@Column(name = "NAME", nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "DESCRIPTION", columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
