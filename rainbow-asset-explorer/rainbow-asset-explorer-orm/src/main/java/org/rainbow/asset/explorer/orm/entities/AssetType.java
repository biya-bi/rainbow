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

import org.rainbow.asset.explorer.orm.audit.AssetTypeAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "ASSET_TYPE")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "AssetType.findAll", query = "SELECT at FROM AssetType at"),
		@NamedQuery(name = "AssetType.findByName", query = "SELECT at FROM AssetType at WHERE UPPER(at.name) = :name"),
		@NamedQuery(name = "AssetType.findById", query = "SELECT at FROM AssetType at WHERE at.id = :id") })
@Auditable(AssetTypeAudit.class)
public class AssetType extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2193610486132836452L;
	private String name;
	private String description;
	private List<Asset> assets;

	public AssetType() {
	}

	public AssetType(Long id) {
		super(id);
	}

	public AssetType(String name) {
		this.name = name;
	}

	public AssetType(String name, Long id) {
		super(id);
		this.name = name;
	}

	public AssetType(String name, String description, Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
		this.name = name;
		this.description = description;
	}

	public AssetType(String name, String description, Date creationDate, Date lastUpdateDate, long version, Long id) {
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

	@OneToMany(mappedBy = "assetType")
	public List<Asset> getAssets() {
		return assets;
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}

}
