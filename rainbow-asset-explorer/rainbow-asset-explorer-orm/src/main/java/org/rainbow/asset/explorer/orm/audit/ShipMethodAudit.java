package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.entities.ShipMethod;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SHIP_METHOD_AUDIT")
public class ShipMethodAudit extends AbstractAuditableEntityAudit<ShipMethod, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5530487405693218217L;
	private String name;
	private String description;

	public ShipMethodAudit() {
	}

	public ShipMethodAudit(ShipMethod shipMethod, WriteOperation writeOperation) {
		super(shipMethod, writeOperation);
		this.name = shipMethod.getName();
		this.description = shipMethod.getDescription();
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
