package org.rainbow.asset.explorer.faces.controllers.details;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.orm.entities.ShippingOrderStatus;
import org.rainbow.asset.explorer.service.services.ShippingOrderService;
import org.rainbow.faces.controllers.details.AbstractAuditableDetailController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "ShippingOrderCreated", updatedMessageKey = "ShippingOrderUpdated", deletedMessageKey = "ShippingOrderDeleted")
public class ShippingOrderDetailController extends AbstractAuditableDetailController<ShippingOrder> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6200481042236885573L;

	private ShippingOrderDetail detail;
	private List<ShippingOrderDetail> receivedDetails;

	@Autowired
	private ShippingOrderService service;

	public ShippingOrderDetailController() {
		super(ShippingOrder.class);
	}

	@Override
	public ShippingOrder prepareCreate() {
		ShippingOrder shippingOrder = super.prepareCreate();
		shippingOrder.setStatus(ShippingOrderStatus.PENDING);
		return shippingOrder;
	}

	public ShippingOrderDetail getDetail() {
		return detail;
	}

	public void setDetail(ShippingOrderDetail detail) {
		this.detail = detail;
	}

	public List<ShippingOrderDetail> getReceivedDetails() {
		return receivedDetails;
	}

	public void setReceivedDetails(List<ShippingOrderDetail> receivedDetails) {
		this.receivedDetails = receivedDetails;
	}

	public void addDetail() {
		ShippingOrder shippingOrder = this.getCurrent();
		if (shippingOrder != null) {
			List<ShippingOrderDetail> details = shippingOrder.getDetails();
			if (details == null) {
				details = new ArrayList<>();
				shippingOrder.setDetails(details);
			}
			details.add(detail);
		}
	}

	public void removeDetail() {
		if (detail != null) {
			ShippingOrder shippingOrder = this.getCurrent();
			if (shippingOrder != null) {
				List<ShippingOrderDetail> details = shippingOrder.getDetails();
				if (details != null) {
					details.remove(detail);
				}
			}
		}
	}

	public void prepareDetail() {
		detail = new ShippingOrderDetail();
		detail.setReceivedQuantity((short) 0);
		detail.setRejectedQuantity((short) 0);
	}

	public boolean canBeApproved() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == ShippingOrderStatus.PENDING;
	}

	public boolean canBeRejected() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == ShippingOrderStatus.PENDING;
	}

	public boolean canBeShipped() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == ShippingOrderStatus.APPROVED;
	}

	public boolean canBeRestituted() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == ShippingOrderStatus.IN_TRANSIT;
	}

	public boolean canBeDelivered() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == ShippingOrderStatus.IN_TRANSIT;
	}

	public void approve() throws Exception {
		service.approve(this.getCurrent());
	}

	public void reject() throws Exception {
		service.reject(this.getCurrent());
	}

	public void ship() throws Exception {
		service.transit(this.getCurrent());
	}

	public void restitute() throws Exception {
		service.restitute(this.getCurrent());
	}

	public void addReceivedDetail() {
		if (detail != null) {
			if (receivedDetails == null) {
				receivedDetails = new ArrayList<>();
			}
			receivedDetails.add(detail);
		}
	}

	public void removeReceivedDetail() {
		if (detail != null) {
			if (receivedDetails != null) {
				receivedDetails.remove(detail);
			}
		}
	}

	public void prepareDeliver() {
		receivedDetails = new ArrayList<>();

		for (ShippingOrderDetail d : this.getCurrent().getDetails()) {
			ShippingOrderDetail receivedDetail = new ShippingOrderDetail();
			receivedDetail.setId(d.getId());
			receivedDetail.setProduct(d.getProduct());
			receivedDetail.setReceivedQuantity(d.getShippedQuantity());
			receivedDetails.add(receivedDetail);
		}
	}

	public void deliver() throws Exception {
		Map<Long, Short> productByQuantities = new HashMap<>();
		for (ShippingOrderDetail receivedDetail : receivedDetails) {
			Long productId = receivedDetail.getProduct().getId();
			if (!productByQuantities.containsKey(productId)) {
				productByQuantities.put(productId, receivedDetail.getReceivedQuantity());
			} else {
				productByQuantities.replace(productId,
						(short) (productByQuantities.get(productId) + receivedDetail.getReceivedQuantity()));
			}
		}
		service.deliver(this.getCurrent(), productByQuantities);
	}

	public void setDetails() throws Exception {
		ShippingOrder shippingOrder = this.getCurrent();
		if (shippingOrder != null) {
			shippingOrder.setDetails(service.getDetails(shippingOrder.getId()));
		}
	}

	@Override
	protected Service<ShippingOrder> getService() {
		return service;
	}
}
