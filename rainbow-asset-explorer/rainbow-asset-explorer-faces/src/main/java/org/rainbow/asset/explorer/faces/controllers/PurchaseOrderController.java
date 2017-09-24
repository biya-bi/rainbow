package org.rainbow.asset.explorer.faces.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrder;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderDetail;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderStatus;
import org.rainbow.asset.explorer.service.services.PurchaseOrderService;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.Service;
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
public class PurchaseOrderController extends AuditableController<PurchaseOrder, Long, SearchOptions> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6934476431314707921L;

	private PurchaseOrderDetail detail;

	private List<PurchaseOrderDetail> receivedDetails;

	private Location receiptLocation;

	@Autowired
	@Qualifier("purchaseOrderService")
	private PurchaseOrderService service;

	public PurchaseOrderController() {
		super(PurchaseOrder.class);
	}

	@Override
	public PurchaseOrder prepareCreate() {
		PurchaseOrder purchaseOrder = super.prepareCreate();
		purchaseOrder.setStatus(PurchaseOrderStatus.PENDING);
		return purchaseOrder;
	}

	public PurchaseOrderDetail getDetail() {
		return detail;
	}

	public void setDetail(PurchaseOrderDetail detail) {
		this.detail = detail;
	}

	public List<PurchaseOrderDetail> getReceivedDetails() {
		return receivedDetails;
	}

	public void setReceivedDetails(List<PurchaseOrderDetail> receivedDetails) {
		this.receivedDetails = receivedDetails;
	}

	public Location getReceiptLocation() {
		return receiptLocation;
	}

	public void setReceiptLocation(Location receiptLocation) {
		this.receiptLocation = receiptLocation;
	}

	public void addDetail() {
		PurchaseOrder purchaseOrder = this.getCurrent();
		if (purchaseOrder != null) {
			List<PurchaseOrderDetail> details = purchaseOrder.getDetails();
			if (details == null) {
				details = new ArrayList<>();
				purchaseOrder.setDetails(details);
			}
			details.add(detail);
		}
	}

	public void removeDetail() {
		if (detail != null) {
			PurchaseOrder purchaseOrder = this.getCurrent();
			if (purchaseOrder != null) {
				List<PurchaseOrderDetail> details = purchaseOrder.getDetails();
				if (details != null) {
					details.remove(detail);
				}
			}
		}
	}

	public void prepareDetail() {
		detail = new PurchaseOrderDetail();
		detail.setReceivedQuantity((short) 0);
		detail.setRejectedQuantity((short) 0);
	}

	public boolean canBeApproved() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == PurchaseOrderStatus.PENDING;
	}

	public boolean canBeRejected() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == PurchaseOrderStatus.PENDING;
	}

	public boolean canBeCompleted() {
		return this.getCurrent() != null && this.getCurrent().getStatus() == PurchaseOrderStatus.APPROVED;
	}

	public void approve() throws Exception {
		service.approve(this.getCurrent());
	}

	public void reject() throws Exception {
		service.reject(this.getCurrent());
	}

	public void complete() throws Exception {
		Map<Long, Short> productByQuantities = new HashMap<>();
		for (PurchaseOrderDetail receivedDetail : receivedDetails) {
			Long productId = receivedDetail.getProduct().getId();
			if (!productByQuantities.containsKey(productId)) {
				productByQuantities.put(productId, receivedDetail.getReceivedQuantity());
			} else {
				productByQuantities.replace(productId,
						(short) (productByQuantities.get(productId) + receivedDetail.getReceivedQuantity()));
			}
		}
		service.complete(this.getCurrent(), this.getReceiptLocation(), productByQuantities);
	}

	public void addReceivedDetail() {
		if (detail != null) {
			if (receivedDetails == null) {
				receivedDetails = new ArrayList<>();
			}
			receivedDetails.add(detail);
		}
	}

	public void removeReceivedDetail(PurchaseOrderDetail detail) {
		if (detail != null) {
			if (receivedDetails != null) {
				receivedDetails.remove(detail);
			}
		}
	}

	public void prepareComplete() {
		receiptLocation = this.getCurrent().getLocation();
		receivedDetails = new ArrayList<>();
		for (PurchaseOrderDetail d : this.getCurrent().getDetails()) {
			PurchaseOrderDetail receivedDetail = new PurchaseOrderDetail();
			receivedDetail.setId(d.getId());
			receivedDetail.setProduct(d.getProduct());
			receivedDetail.setReceivedQuantity(d.getOrderedQuantity());
			receivedDetails.add(receivedDetail);
		}
	}

	public void setDetails() throws Exception {
		PurchaseOrder purchaseOrder = this.getCurrent();
		if (purchaseOrder != null) {
			purchaseOrder.setDetails(service.getDetails(purchaseOrder.getId()));
		}
	}

	@Override
	protected Service<PurchaseOrder, Long, SearchOptions> getService() {
		return service;
	}
}
