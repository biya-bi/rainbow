/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.LocationAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Location.findAll", query = "SELECT l FROM Location l"),
    @NamedQuery(name = "Location.findByName", query = "SELECT l FROM Location l WHERE UPPER(l.name) = :name"),
    @NamedQuery(name = "Location.findById", query = "SELECT l FROM Location l WHERE l.id = :id")})
@EntityListeners(AuditAdapter.class)
@Auditable(audit = LocationAudit.class)
public class Location extends Trackable<Long> {

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

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.Location[ id=" + getId() + " ]";
    }
}
