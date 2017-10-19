package org.rainbow.asset.explorer.orm.audit;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderStatus;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SHIPPING_ORDER_AUDIT")
public class ShippingOrderAudit extends DocumentAudit<ShippingOrder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4647849549744706096L;
	private ShippingOrderStatus status;
	private Long sourceLocationId;
	private Long targetLocationId;
	private Long shipMethodId;
	private Date shipDate;
	private Date deliveryDate;

	public ShippingOrderAudit() {
	}

	public ShippingOrderAudit(ShippingOrder shippingOrder, WriteOperation writeOperation) {
		super(shippingOrder, writeOperation);
		this.status = shippingOrder.getStatus();
		this.sourceLocationId = shippingOrder.getSourceLocation().getId();
		this.targetLocationId = shippingOrder.getTargetLocation().getId();
		this.shipMethodId = shippingOrder.getShipMethod().getId();
		this.shipDate = shippingOrder.getShipDate();
		this.deliveryDate = shippingOrder.getDeliveryDate();
	}

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	public ShippingOrderStatus getStatus() {
		return status;
	}

	public void setStatus(ShippingOrderStatus status) {
		this.status = status;
	}

	@NotNull
	@Column(name = "SOURCE_LOCATION_ID", nullable = false)
	public Long getSourceLocationId() {
		return sourceLocationId;
	}

	public void setSourceLocationId(Long sourceLocationId) {
		this.sourceLocationId = sourceLocationId;
	}

	@NotNull
	@Column(name = "TARGET_LOCATION_ID", nullable = false)
	public Long getTargetLocationId() {
		return targetLocationId;
	}

	public void setTargetLocationId(Long targetLocationId) {
		this.targetLocationId = targetLocationId;
	}

	@NotNull
	@Column(name = "SHIP_METHOD_ID", nullable = false)
	public Long getShipMethodId() {
		return shipMethodId;
	}

	public void setShipMethodId(Long shipMethodId) {
		this.shipMethodId = shipMethodId;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "SHIP_DATE")
	public Date getShipDate() {
		return shipDate;
	}

	public void setShipDate(Date shipDate) {
		this.shipDate = shipDate;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DELIVERY_DATE")
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

}
