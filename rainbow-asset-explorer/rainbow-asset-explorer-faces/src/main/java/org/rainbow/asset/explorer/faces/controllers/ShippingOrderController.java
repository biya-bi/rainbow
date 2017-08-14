/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.core.entities.ShippingOrder;
import org.rainbow.asset.explorer.core.entities.ShippingOrderDetail;
import org.rainbow.asset.explorer.core.entities.ShippingOrderDetailId;
import org.rainbow.asset.explorer.core.entities.ShippingOrderStatus;
import org.rainbow.asset.explorer.core.service.ShippingOrderService;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class ShippingOrderController extends TrackableController<ShippingOrder, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6200481042236885573L;

	private ShippingOrderDetail detail;
	private List<ShippingOrderDetail> receivedDetails;

	@Autowired
	@Qualifier("shippingOrderService")
	private ShippingOrderService service;

	public ShippingOrderController() {
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
		String username = getUserName();
		ShippingOrder shippingOrder = this.getCurrent();
		shippingOrder.setUpdater(username);
		service.approve(shippingOrder);
	}

	public void reject() throws Exception {
		String username = getUserName();
		ShippingOrder shippingOrder = this.getCurrent();
		shippingOrder.setUpdater(username);
		service.reject(shippingOrder);
	}

	public void ship() throws Exception {
		String username = getUserName();
		ShippingOrder shippingOrder = this.getCurrent();
		shippingOrder.setUpdater(username);
		service.transit(shippingOrder);
	}

	public void restitute() throws Exception {
		String username = getUserName();
		ShippingOrder shippingOrder = this.getCurrent();
		shippingOrder.setUpdater(username);
		service.restitute(shippingOrder);
	}

	private int getDetailId() {
		List<Integer> detailIds = new ArrayList<>();
		for (ShippingOrderDetail d : receivedDetails) {
			Integer detailId = d.getId().getDetailId();
			if (!detailIds.contains(detailId)) {
				detailIds.add(detailId);
			}
		}
		int detailId = 0;
		while (detailId++ < receivedDetails.size()) {
			if (!detailIds.contains(detailId)) {
				break;
			}
		}
		return detailId;
	}

	public void addReceivedDetail() {
		if (detail != null) {
			if (receivedDetails == null) {
				receivedDetails = new ArrayList<>();
			}
			detail.setId(new ShippingOrderDetailId(this.getCurrent().getId(), getDetailId()));
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
			ShippingOrderDetailId shippingOrderDetailId = d.getId();
			ShippingOrderDetail receivedDetail = new ShippingOrderDetail();
			receivedDetail.setId(new ShippingOrderDetailId(shippingOrderDetailId.getShippingOrderId(),
					shippingOrderDetailId.getDetailId()));
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
		String username = getUserName();
		ShippingOrder shippingOrder = this.getCurrent();
		shippingOrder.setUpdater(username);
		service.deliver(shippingOrder, productByQuantities);
	}

	public void setDetails() throws Exception {
		ShippingOrder shippingOrder = this.getCurrent();
		if (shippingOrder != null) {
			shippingOrder.setDetails(service.getDetails(shippingOrder.getId()));
		}
	}

	@Override
	protected Service<ShippingOrder, Long, SearchOptions> getService() {
		return service;
	}
}
