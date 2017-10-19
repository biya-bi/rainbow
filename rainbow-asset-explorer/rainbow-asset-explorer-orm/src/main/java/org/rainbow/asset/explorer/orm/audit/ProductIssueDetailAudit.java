package org.rainbow.asset.explorer.orm.audit;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.orm.entities.ProductIssueDetailId;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_ISSUE_DETAIL_AUDIT")
public class ProductIssueDetailAudit extends AbstractAuditableEntityAudit<ProductIssueDetail, ProductIssueDetailId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1649013407527189874L;
	private Long productIssueId;
	private Long productId;
	private Short quantity;

	public ProductIssueDetailAudit() {
	}

	public ProductIssueDetailAudit(ProductIssueDetail productIssueDetail, WriteOperation writeOperation) {
		super(productIssueDetail, writeOperation);
		Objects.requireNonNull(productIssueDetail.getProductIssue());
		Objects.requireNonNull(productIssueDetail.getProduct());
		this.productIssueId = productIssueDetail.getProductIssue().getId();
		this.productId = productIssueDetail.getProduct().getId();
		this.quantity = productIssueDetail.getQuantity();
	}

	@Override
	@Embedded
	public ProductIssueDetailId getId() {
		return super.getId();
	}

	@Override
	public void setId(ProductIssueDetailId id) {
		super.setId(id);
	}

	@NotNull
	@Column(name = "PRODUCT_ISSUE_ID", nullable = false)
	public Long getProductIssueId() {
		return productIssueId;
	}

	public void setProductIssueId(Long productIssueId) {
		this.productIssueId = productIssueId;
	}

	@NotNull
	@Column(name = "PRODUCT_ID", nullable = false)
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	@NotNull
	@Min(1)
	@Column(name = "QUANTITY", nullable = false)
	public Short getQuantity() {
		return quantity;
	}

	public void setQuantity(Short quantity) {
		this.quantity = quantity;
	}

}
