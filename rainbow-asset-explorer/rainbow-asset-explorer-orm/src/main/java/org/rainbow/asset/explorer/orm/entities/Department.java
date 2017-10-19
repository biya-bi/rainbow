package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.audit.DepartmentAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity

@Auditable(DepartmentAudit.class)
public class Department extends AbstractNumericIdAuditableEntity<Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3016275875845003263L;
	private String name;
	private String description;
	private List<ProductIssue> productIssues;

	public Department() {
	}

	public Department(Integer id) {
		super(id);
	}

	public Department(Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
	}

	public Department(Date creationDate, Date lastUpdateDate, long version, Integer id) {
		super(creationDate, lastUpdateDate, version, id);
	}

	public Department(String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate);
	}

	public Department(String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version,
			Integer id) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
	}

	public Department(String name, String description, String createdBy, String updatedBy, Date creationDate,
			Date lastUpdateDate) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate);
		this.name = name;
		this.description = description;
	}

	public Department(String name, String description, String createdBy, String updatedBy, Date creationDate,
			Date lastUpdateDate, long version, Integer id) {
		super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
		this.name = name;
		this.description = description;
	}

	@Basic(optional = false)
	@NotNull
	@Size(min = 1)
	@Column(nullable = false, unique = true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(columnDefinition = "TEXT")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToMany(mappedBy = "department")
	public List<ProductIssue> getProductIssues() {
		return productIssues;
	}

	public void setProductIssues(List<ProductIssue> productIssues) {
		this.productIssues = productIssues;
	}

}
