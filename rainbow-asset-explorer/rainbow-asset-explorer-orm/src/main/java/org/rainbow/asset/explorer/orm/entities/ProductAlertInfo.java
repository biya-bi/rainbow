package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "PRODUCT_ALERT_INFO")
public class ProductAlertInfo extends Trackable<ProductAlertInfoId> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5795907501784789868L;
	private AlertType alertType;
	private Short reorderPoint;
	private Short availableQuantity;
	private Date alertDate;
	private Product product;
	private Location location;

	public ProductAlertInfo() {
	}

	public ProductAlertInfo(ProductAlertInfoId id) {
		super(id);
	}

	@Override
	@EmbeddedId
	public ProductAlertInfoId getId() {
		return super.getId();
	}

	@Override
	public void setId(ProductAlertInfoId id) {
		super.setId(id);
	}

	@Column(name = "ALERT_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public AlertType getAlertType() {
		return alertType;
	}

	public void setAlertType(AlertType alertType) {
		this.alertType = alertType;
	}

	@Min(1)
	@NotNull
	@Column(name = "REORDER_POINT", nullable = false)
	public Short getReorderPoint() {
		return reorderPoint;
	}

	public void setReorderPoint(Short reorderPoint) {
		this.reorderPoint = reorderPoint;
	}

	@Min(1)
	@NotNull
	@Column(name = "AVAILABLE_QUANTITY", nullable = false)
	public Short getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Short availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALERT_DATE", nullable = false)
	public Date getAlertDate() {
		return alertDate;
	}

	public void setAlertDate(Date alertDate) {
		this.alertDate = alertDate;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "PRODUCT_ID", insertable = false, updatable = false)
	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "LOCATION_ID", insertable = false, updatable = false)
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
