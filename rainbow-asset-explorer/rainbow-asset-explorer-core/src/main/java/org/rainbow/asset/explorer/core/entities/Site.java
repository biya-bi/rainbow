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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.SiteAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Site.findAll", query = "SELECT s FROM Site s"),
    @NamedQuery(name = "Site.findByName", query = "SELECT s FROM Site s WHERE UPPER(s.name) = :name"),
    @NamedQuery(name = "Site.findById", query = "SELECT s FROM Site s WHERE s.id = :id")})
@EntityListeners(AuditAdapter.class)
@Auditable(audit = SiteAudit.class)
public class Site extends Trackable<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1510359712135789346L;
	private String name;
    private String description;
    private String address;
    private String location;
    private SiteStatus status = SiteStatus.NEW;
    private Date dateOfCommissioning;
    private Date dateOfDecommissioning;
    private List<SiteDocument> documents;
    private List<Asset> assets;

    public Site() {
    }

    public Site(Long id) {
        super(id);
    }

    public Site(String name) {
        this.name = name;
    }

    public Site(String name, Long id) {
        super(id);
        this.name = name;
    }

    public Site(String name, String description, Date creationDate, Date lastUpdateDate) {
        super(creationDate, lastUpdateDate);
        this.name = name;
        this.description = description;
    }

    public Site(String name, String description, Date creationDate, Date lastUpdateDate, long version, Long id) {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public SiteStatus getStatus() {
        return status;
    }

    public void setStatus(SiteStatus status) {
        this.status = status;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_COMMISSIONING")
    public Date getDateOfCommissioning() {
        return dateOfCommissioning;
    }

    public void setDateOfCommissioning(Date dateOfCommissioning) {
        this.dateOfCommissioning = dateOfCommissioning;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DATE_OF_DECOMMISSIONING")
    public Date getDateOfDecommissioning() {
        return dateOfDecommissioning;
    }

    public void setDateOfDecommissioning(Date dateOfDecommissioning) {
        this.dateOfDecommissioning = dateOfDecommissioning;
    }

    @OneToMany(mappedBy = "site")
    public List<SiteDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<SiteDocument> documents) {
        this.documents = documents;
    }

    @OneToMany(mappedBy = "site")
    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.Site[ id=" + getId() + " ]";
    }
}
