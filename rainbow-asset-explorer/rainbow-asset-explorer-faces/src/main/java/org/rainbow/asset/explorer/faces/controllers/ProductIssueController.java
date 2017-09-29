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
import org.rainbow.asset.explorer.orm.entities.ProductIssue;
import org.rainbow.asset.explorer.orm.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductIssueReferenceNumberException;
import org.rainbow.asset.explorer.service.exceptions.InsufficientInventoryException;
import org.rainbow.asset.explorer.service.exceptions.ProductIssueDetailsNullOrEmptyException;
import org.rainbow.asset.explorer.service.services.ProductIssueService;
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
@CrudNotificationInfo(createdMessageKey = "ProductIssueCreated", updatedMessageKey = "ProductIssueUpdated", deletedMessageKey = "ProductIssueDeleted")
public class ProductIssueController extends AuditableController<ProductIssue, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3906559360437655759L;

	private ProductIssueDetail detail = new ProductIssueDetail();

	private static final String DUPLICATE_PRODUCT_ISSUE_REFERENCE_NUMBER_ERROR_KEY = "DuplicateProductIssueReferenceNumber";
	private static final String PRODUCT_ISSUE_DETAILS_EMPTY_ERROR_KEY = "ProductIssueDetailsEmpty";
	private static final String INSUFFICIENT_INVENTORY_ERROR_KEY = "InsufficientInventory";

	@Autowired
	@Qualifier("productIssueService")
	private ProductIssueService service;

	@Autowired
	@Qualifier("locationService")
	private Service<Location, Long, SearchOptions> locationService;

	@Autowired
	@Qualifier("productService")
	private Service<Product, Long, SearchOptions> productService;

	public ProductIssueController() {
		super(ProductIssue.class);
	}

	public ProductIssueDetail getDetail() {
		return detail;
	}

	public void setDetail(ProductIssueDetail detail) {
		this.detail = detail;
	}

	public void addDetail() {
		ProductIssue productIssue = this.getCurrent();
		if (productIssue != null) {
			List<ProductIssueDetail> details = productIssue.getDetails();
			if (details == null) {
				details = new ArrayList<>();
				productIssue.setDetails(details);
			}
			details.add(detail);
		}
	}

	public void removeDetail() {
		if (detail != null) {
			ProductIssue productIssue = this.getCurrent();
			if (productIssue != null) {
				List<ProductIssueDetail> details = productIssue.getDetails();
				if (details != null) {
					details.remove(detail);
				}
			}
		}
	}

	public void prepareDetail() {
		detail = new ProductIssueDetail();
	}

	@Override
	public ProductIssue prepareCreate() {
		ProductIssue productIssue = super.prepareCreate();
		productIssue.setIssueDate(new Date());
		return productIssue;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateProductIssueReferenceNumberException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateProductIssueReferenceNumberException e = (DuplicateProductIssueReferenceNumberException) throwable;
			FacesContextUtil.addErrorMessage(String.format(ResourceBundle.getBundle(CRUD_MESSAGES)
					.getString(DUPLICATE_PRODUCT_ISSUE_REFERENCE_NUMBER_ERROR_KEY), e.getReferenceNumber()));
			return true;
		} else if (throwable instanceof ProductIssueDetailsNullOrEmptyException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			FacesContextUtil.addErrorMessage(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(PRODUCT_ISSUE_DETAILS_EMPTY_ERROR_KEY));
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
		ProductIssue productIssue = this.getCurrent();
		if (productIssue != null) {
			productIssue.setDetails(service.getDetails(productIssue.getId()));
		}
	}

	@Override
	protected Service<ProductIssue, Long, SearchOptions> getService() {
		return service;
	}
}
