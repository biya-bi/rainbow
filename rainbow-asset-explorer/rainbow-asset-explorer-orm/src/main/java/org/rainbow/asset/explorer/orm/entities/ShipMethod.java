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
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.adapters.AuditAdapter;
import org.rainbow.asset.explorer.orm.audit.Auditable;
import org.rainbow.asset.explorer.orm.audit.ShipMethodAudit;
import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SHIP_METHOD", uniqueConstraints = @UniqueConstraint(columnNames = { "NAME" }))
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ShipMethodAudit.class)
public class ShipMethod extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -916333520995689442L;
	private String name;
	private String description;
	private List<PurchaseOrder> purchaseOrders;
	private List<ShippingOrder> shippingOrders;

	public ShipMethod() {
	}

	public ShipMethod(Long id) {
		super(id);
	}

	public ShipMethod(String name, Long id) {
		super(id);
		this.name = name;
	}

	public ShipMethod(String name) {
		this.name = name;
	}

	public ShipMethod(String name, Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
		this.name = name;
	}

	public ShipMethod(String name, Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creationDate, lastUpdateDate, version, id);
		this.name = name;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
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

	@Column(name = "DESCRIPTION", columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "shipMethod")
	public List<PurchaseOrder> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
	}

	@OneToMany(mappedBy = "shipMethod")
	public List<ShippingOrder> getShippingOrders() {
		return shippingOrders;
	}

	public void setShippingOrders(List<ShippingOrder> shippingOrders) {
		this.shippingOrders = shippingOrders;
	}

}
