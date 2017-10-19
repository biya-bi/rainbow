package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.rainbow.asset.explorer.orm.audit.LocationAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
		@NamedQuery(name = "Location.findByName", query = "SELECT l FROM Location l WHERE UPPER(l.name) = :name"),
		@NamedQuery(name = "Location.findById", query = "SELECT l FROM Location l WHERE l.id = :id") })

@Auditable(LocationAudit.class)
public class Location extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6474824889653028966L;
	private String name;
	private String description;
	private List<ShippingOrder> outboundShippingOrders;
	private List<ShippingOrder> inboundShippingOrders;
	private List<PurchaseOrder> purchaseOrders;

	public Location() {
	}

	public Location(Long id) {
		super(id);
	}

	public Location(String name) {
		this.name = name;
	}

	public Location(String name, Long id) {
		super(id);
		this.name = name;
	}

	public Location(String name, String description, Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
		this.name = name;
		this.description = description;
	}

	public Location(String name, String description, Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creationDate, lastUpdateDate, version, id);
		this.name = name;
		this.description = description;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(name = "NAME", nullable = false, unique = true)
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

	@OneToMany(mappedBy = "sourceLocation")
	public List<ShippingOrder> getOutboundShippingOrders() {
		return outboundShippingOrders;
	}

	public void setOutboundShippingOrders(List<ShippingOrder> outboundShippingOrders) {
		this.outboundShippingOrders = outboundShippingOrders;
	}

	@OneToMany(mappedBy = "targetLocation")
	public List<ShippingOrder> getInboundShippingOrders() {
		return inboundShippingOrders;
	}

	public void setInboundShippingOrders(List<ShippingOrder> inboundShippingOrders) {
		this.inboundShippingOrders = inboundShippingOrders;
	}

	@OneToMany(mappedBy = "location")
	public List<PurchaseOrder> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
		this.purchaseOrders = purchaseOrders;
	}

}
