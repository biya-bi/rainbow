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

import org.rainbow.asset.explorer.orm.audit.ManufacturerAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "MANUFACTURER")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Manufacturer.findAll", query = "SELECT m FROM Manufacturer m"),
		@NamedQuery(name = "Manufacturer.findByName", query = "SELECT m FROM Manufacturer m WHERE UPPER(m.name) = :name"),
		@NamedQuery(name = "Manufacturer.findById", query = "SELECT m FROM Manufacturer m WHERE m.id = :id") })

@Auditable(ManufacturerAudit.class)
public class Manufacturer extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4451928134964529078L;
	private String name;
	private String description;
	private List<Product> products;

	public Manufacturer() {
	}

	public Manufacturer(Long id) {
		super(id);
	}

	public Manufacturer(String name) {
		this.name = name;
	}

	public Manufacturer(String name, Long id) {
		super(id);
		this.name = name;
	}

	public Manufacturer(String name, String description, Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
		this.name = name;
		this.description = description;
	}

	public Manufacturer(String name, String description, Date creationDate, Date lastUpdateDate, long version,
			Long id) {
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

	@OneToMany(mappedBy = "manufacturer")
	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

}
