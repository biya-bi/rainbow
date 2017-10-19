package org.rainbow.asset.explorer.orm.audit;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetail;
import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetailId;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_RECEIPT_DETAIL_AUDIT")
public class ProductReceiptDetailAudit extends AbstractAuditableEntityAudit<ProductReceiptDetail, ProductReceiptDetailId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 630231182397332736L;
	private Long productReceiptId;
	private Long productId;
	private Short quantity;
	private Double unitPrice;

	public ProductReceiptDetailAudit() {
	}

	public ProductReceiptDetailAudit(ProductReceiptDetail productReceiptDetail, WriteOperation writeOperation) {
		super(productReceiptDetail, writeOperation);
		Objects.requireNonNull(productReceiptDetail.getProductReceipt());
		Objects.requireNonNull(productReceiptDetail.getProduct());
		this.productReceiptId = productReceiptDetail.getProductReceipt().getId();
		this.productId = productReceiptDetail.getProduct().getId();
		this.quantity = productReceiptDetail.getQuantity();
		this.unitPrice = productReceiptDetail.getUnitPrice();
	}

	@Override
	@Embedded
	public ProductReceiptDetailId getId() {
		return super.getId();
	}

	@Override
	public void setId(ProductReceiptDetailId id) {
		super.setId(id);
	}

	@NotNull
	@Column(name = "PRODUCT_RECEIPT_ID", nullable = false)
	public Long getProductReceiptId() {
		return productReceiptId;
	}

	public void setProductReceiptId(Long productReceiptId) {
		this.productReceiptId = productReceiptId;
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

	@NotNull
	@Min(0)
	@Column(name = "UNIT_PRICE", nullable = false)
	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

}
