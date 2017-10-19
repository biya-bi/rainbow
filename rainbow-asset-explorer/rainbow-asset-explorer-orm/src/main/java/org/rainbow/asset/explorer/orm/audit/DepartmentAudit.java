package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.entities.Department;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

@Entity
@Table(name = "DEPARTMENT_AUDIT")
public class DepartmentAudit extends AbstractAuditableEntityAudit<Department, Integer> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9159281403613071586L;
	private String name;
	private String description;

	public DepartmentAudit() {
	}

	public DepartmentAudit(Department department, WriteOperation writeOperation) {
		super(department, writeOperation);
		this.name = department.getName();
		this.description = department.getDescription();
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
}
