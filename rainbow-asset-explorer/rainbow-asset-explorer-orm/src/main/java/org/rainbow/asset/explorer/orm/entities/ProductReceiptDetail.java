package org.rainbow.asset.explorer.orm.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.audit.ProductReceiptDetailAudit;
import org.rainbow.orm.audit.Auditable;
import org.rainbow.orm.entities.AbstractAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_RECEIPT_DETAIL")

@Auditable(ProductReceiptDetailAudit.class)
public class ProductReceiptDetail extends AbstractAuditableEntity<ProductReceiptDetailId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8128194326239731611L;
	private ProductReceipt productReceipt;
	private Product product;
	private Short quantity;
	private Double unitPrice;

	public ProductReceiptDetail() {
	}

	@Override
	@EmbeddedId
	public ProductReceiptDetailId getId() {
		return super.getId();
	}

	@Override
	public void setId(ProductReceiptDetailId id) {
		super.setId(id);
	}

	@ManyToOne
	@JoinColumn(name = "PRODUCT_RECEIPT_ID", nullable = false)
	@NotNull
	public ProductReceipt getProductReceipt() {
		return productReceipt;
	}

	public void setProductReceipt(ProductReceipt productReceipt) {
		this.productReceipt = productReceipt;
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

	@Transient
	public double getSubTotal() {
		if (quantity != null && unitPrice != null) {
			return quantity * unitPrice;
		}
		return 0;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 23 * hash + Objects.hashCode(this.getId());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ProductReceiptDetail other = (ProductReceiptDetail) obj;
		if (!Objects.equals(this.getId(), other.getId())) {
			return false;
		}
		return true;
	}

}
