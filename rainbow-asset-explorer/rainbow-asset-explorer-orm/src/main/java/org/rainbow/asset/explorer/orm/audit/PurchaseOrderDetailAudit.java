package org.rainbow.asset.explorer.orm.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetail;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetailId;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PURCHASE_ORDER_DETAIL_AUDIT")
public class PurchaseOrderDetailAudit extends TrackableAudit<PurchaseOrderDetail, PurchaseOrderDetailId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2350235378710757398L;
	private Long productId;
	private Date dueDate;
	private Short orderedQuantity;
	private Double unitPrice;
	private Short receivedQuantity;
	private Short rejectedQuantity;

	public PurchaseOrderDetailAudit() {
	}

	public PurchaseOrderDetailAudit(PurchaseOrderDetail purchaseOrderDetail, WriteOperation writeOperation) {
		super(purchaseOrderDetail, writeOperation);
		this.productId = purchaseOrderDetail.getProduct().getId();
		this.dueDate = purchaseOrderDetail.getDueDate();
		this.orderedQuantity = purchaseOrderDetail.getOrderedQuantity();
		this.unitPrice = purchaseOrderDetail.getUnitPrice();
		this.receivedQuantity = purchaseOrderDetail.getReceivedQuantity();
		this.rejectedQuantity = purchaseOrderDetail.getRejectedQuantity();
	}

	@Override
	@Embedded
	public PurchaseOrderDetailId getId() {
		return super.getId();
	}

	@Override
	public void setId(PurchaseOrderDetailId id) {
		super.setId(id);
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
	@Temporal(TemporalType.DATE)
	@Column(name = "DUE_DATE", nullable = false)
	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	@NotNull
	@Min(1)
	@Column(name = "ORDERED_QUANTITY", nullable = false)
	public Short getOrderedQuantity() {
		return orderedQuantity;
	}

	public void setOrderedQuantity(Short orderedQuantity) {
		this.orderedQuantity = orderedQuantity;
	}

	@NotNull
	@Min(0)
	@Column(name = "UNIT_PRICE", nullable = false)
	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	@NotNull
	@Min(0)
	@Column(name = "RECEIVED_QUANTITY", nullable = false)
	public Short getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(Short receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	@NotNull
	@Min(0)
	@Column(name = "REJECTED_QUANTITY", nullable = false)
	public Short getRejectedQuantity() {
		return rejectedQuantity;
	}

	public void setRejectedQuantity(Short rejectedQuantity) {
		this.rejectedQuantity = rejectedQuantity;
	}

	@Override
	public String toString() {
		return "org.rainbow.asset.explorer.core.audit.PurchaseOrderDetailAudit[ auditId=" + getAuditId() + " ]";
	}
}
