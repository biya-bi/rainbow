package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.orm.entities.ProductReceipt;
import org.rainbow.asset.explorer.orm.entities.ProductReceiptDetail;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductReceiptReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.service.exceptions.ProductReceiptDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.service.services.ProductReceiptService;
import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.services.Service;
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
@CrudNotificationInfo(createdMessageKey = "ProductReceiptCreated", updatedMessageKey = "ProductReceiptUpdated", deletedMessageKey = "ProductReceiptDeleted")
public class ProductReceiptController extends AuditableController<ProductReceipt, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6177634125211864091L;

	private ProductReceiptDetail detail = new ProductReceiptDetail();

	private static final String DUPLICATE_PRODUCT_RECEIPT_REFERENCE_NUMBER_ERROR_KEY = "DuplicateProductReceiptReferenceNumber";
	private static final String PRODUCT_RECEIPT_DETAILS_EMPTY_ERROR_KEY = "ProductReceiptDetailsEmpty";
	private static final String INSUFFICIENT_INVENTORY_ERROR_KEY = "InsufficientInventory";

	@Autowired
	@Qualifier("productReceiptService")
	private ProductReceiptService service;

	@Autowired
	@Qualifier("locationService")
	private Service<Location, Long, SearchOptions> locationService;

	@Autowired
	@Qualifier("productService")
	private Service<Product, Long, SearchOptions> productService;

	public ProductReceiptController() {
		super(ProductReceipt.class);
	}

	public ProductReceiptDetail getDetail() {
		return detail;
	}

	public void setDetail(ProductReceiptDetail detail) {
		this.detail = detail;
	}

	public void addDetail() {
		ProductReceipt productReceipt = this.getCurrent();
		if (productReceipt != null) {
			List<ProductReceiptDetail> details = productReceipt.getDetails();
			if (details == null) {
				details = new ArrayList<>();
				productReceipt.setDetails(details);
			}
			details.add(detail);
		}
	}

	public void removeDetail() {
		if (detail != null) {
			ProductReceipt productReceipt = this.getCurrent();
			if (productReceipt != null) {
				List<ProductReceiptDetail> details = productReceipt.getDetails();
				if (details != null) {
					details.remove(detail);
				}
			}
		}
	}

	public void prepareDetail() {
		detail = new ProductReceiptDetail();
	}

	@Override
	public ProductReceipt prepareCreate() {
		ProductReceipt productReceipt = super.prepareCreate();
		productReceipt.setReceiptDate(new Date());
		return productReceipt;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateProductReceiptReferenceNumberException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateProductReceiptReferenceNumberException e = (DuplicateProductReceiptReferenceNumberException) throwable;
			FacesContextUtil.addErrorMessage(String.format(ResourceBundle.getBundle(CRUD_MESSAGES)
					.getString(DUPLICATE_PRODUCT_RECEIPT_REFERENCE_NUMBER_ERROR_KEY), e.getReferenceNumber()));
			return true;
		} else if (throwable instanceof ProductReceiptDetailsNullOrEmptyException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			FacesContextUtil.addErrorMessage(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(PRODUCT_RECEIPT_DETAILS_EMPTY_ERROR_KEY));
			return true;
		} else if (throwable instanceof InsufficientInventoryException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			InsufficientInventoryException e = (InsufficientInventoryException) throwable;
			Location location;
			Product product;
			try {
				location = locationService.findById(e.getLocationId());
				product = productService.findById(e.getProductId());
			} catch (Exception e1) {
				return super.handle(e1);
			}

			FacesContextUtil.addErrorMessage(
					String.format(ResourceBundle.getBundle(CRUD_MESSAGES).getString(INSUFFICIENT_INVENTORY_ERROR_KEY),
							location.getName(), e.getAvailableQuantity(), product.getName(), e.getRequestedQuantity()));
			return true;
		}
		return super.handle(throwable);
	}

	public void setDetails() throws Exception {
		ProductReceipt productReceipt = this.getCurrent();
		if (productReceipt != null) {
			productReceipt.setDetails(service.getDetails(productReceipt.getId()));
		}
	}

	@Override
	protected Service<ProductReceipt, Long, SearchOptions> getService() {
		return service;
	}
}
