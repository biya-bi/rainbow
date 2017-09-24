package org.rainbow.asset.explorer.orm.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.entities.ProductIssue;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_ISSUE_AUDIT")
public class ProductIssueAudit extends DocumentAudit<ProductIssue> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7636803528183039456L;
	private Long locationId;
	private Integer departmentId;
	private String requisitioner;
	private Date issueDate;

	public ProductIssueAudit() {
	}

	public ProductIssueAudit(ProductIssue productIssue, WriteOperation writeOperation) {
		super(productIssue, writeOperation);
		this.locationId = productIssue.getLocation().getId();
		this.departmentId = productIssue.getDepartment().getId();
		this.requisitioner = productIssue.getRequisitioner();
		this.issueDate = productIssue.getIssueDate();
	}

	@NotNull
	@Column(name = "LOCATION_ID", nullable = false)
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	@JoinColumn(name = "DEPARTMENT_ID")
	public Integer getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(Integer departmentId) {
		this.departmentId = departmentId;
	}

	@Column(nullable = false)
	public String getRequisitioner() {
		return requisitioner;
	}

	public void setRequisitioner(String requisitioner) {
		this.requisitioner = requisitioner;
	}

	@NotNull
	@Temporal(TemporalType.DATE)
	@Column(name = "ISSUE_DATE", nullable = false)
	public Date getIssueDate() {
		return issueDate;
	}

	public void setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
	}

	@Override
	public String toString() {
		return "org.rainbow.asset.explorer.core.audit.ProductIssueAudit[ auditId=" + getAuditId() + " ]";
	}

}
