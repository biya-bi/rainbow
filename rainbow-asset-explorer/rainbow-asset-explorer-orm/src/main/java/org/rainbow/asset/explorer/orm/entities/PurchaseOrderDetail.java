package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.adapters.AuditAdapter;
import org.rainbow.asset.explorer.orm.audit.Auditable;
import org.rainbow.asset.explorer.orm.audit.PurchaseOrderDetailAudit;
import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PURCHASE_ORDER_DETAIL")
@EntityListeners(AuditAdapter.class)
@Auditable(audit = PurchaseOrderDetailAudit.class)
public class PurchaseOrderDetail extends Trackable<PurchaseOrderDetailId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8364995538178938871L;
	private PurchaseOrder purchaseOrder;
	private Product product;
	private Date dueDate;
	private Short orderedQuantity;
	private double unitPrice;
	private Short receivedQuantity;
	private Short rejectedQuantity;

	public PurchaseOrderDetail() {
	}

	@Override
	@EmbeddedId
	public PurchaseOrderDetailId getId() {
		return super.getId();
	}

	@Override
	public void setId(PurchaseOrderDetailId id) {
		super.setId(id);
	}

	@ManyToOne
	@JoinColumn(name = "PURCHASE_ORDER_ID", nullable = false)
	@NotNull
	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}

	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}

	@NotNull
	@ManyToOne
	@JoinColumn(name = "PRODUCT_ID", nullable = false)
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
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
	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
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
}
