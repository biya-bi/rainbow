/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.controller;

import static org.rainbow.shopping.cart.ui.web.utilities.ResourceBundles.CRUD_MESSAGES;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.rainbow.shopping.cart.core.entities.Photo;
import org.rainbow.shopping.cart.core.entities.Product;
import org.rainbow.shopping.cart.core.persistence.exceptions.DuplicateProductCodeException;
import org.rainbow.shopping.cart.core.persistence.exceptions.DuplicateProductNameException;
import org.rainbow.shopping.cart.ui.web.utilities.BytesToImageConverter;
import org.rainbow.shopping.cart.ui.web.utilities.CrudNotificationInfo;
import org.rainbow.shopping.cart.ui.web.utilities.JsfUtil;
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
@CrudNotificationInfo(createdMessageKey = "ProductCreated", updatedMessageKey = "ProductUpdated", deletedMessageKey = "ProductDeleted")
public class ProductController extends TrackableController<Product, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1423310559956400949L;

	@Autowired
	@Qualifier("productService")
	private Service<Product, Long, SearchOptions> service;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	private static final String DUPLICATE_PRODUCT_NUMBER_ERROR_KEY = "DuplicateProductCode";
	private static final String DUPLICATE_PRODUCT_NAME_ERROR_KEY = "DuplicateProductName";

	public ProductController() {
		super(Product.class);
	}

	@Override
	protected Service<Product, Long, SearchOptions> getService() {
		return service;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateProductCodeException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateProductCodeException e = (DuplicateProductCodeException) throwable;
			JsfUtil.addErrorMessage(
					String.format(ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_PRODUCT_NUMBER_ERROR_KEY),
							e.getCode()));
			return true;
		} else if (throwable instanceof DuplicateProductNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateProductNameException e = (DuplicateProductNameException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_PRODUCT_NAME_ERROR_KEY), e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	public void uploadPhoto(FileUploadEvent event) {
		if (this.getCurrent() != null) {
			if (this.getCurrent().getPhoto() == null)
				this.getCurrent().setPhoto(new Photo());
			Photo photo = this.getCurrent().getPhoto();
			photo.setFileName(event.getFile().getFileName());
			photo.setFileContent(event.getFile().getContents());
			photo.setFileContentType(event.getFile().getContentType());
			photo.setFileSize(event.getFile().getSize());
		}
	}

	public StreamedContent getPhoto() throws IOException {
		if (this.getCurrent() == null || this.getCurrent().getPhoto() == null)
			return new DefaultStreamedContent();
		return bytesToImageConverter.getImage(this.getCurrent().getPhoto());
	}

}
