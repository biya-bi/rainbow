/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.ui.web.controller;

import static org.rainbow.shopping.cart.ui.web.utilities.ResourceBundles.CRUD_MESSAGES;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.persistence.dao.impl.exceptions.DuplicateProductCodeException;
import org.rainbow.persistence.dao.impl.exceptions.DuplicateProductNameException;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.model.File;
import org.rainbow.shopping.cart.model.Product;
import org.rainbow.shopping.cart.ui.web.utilities.BytesToImageConverter;
import org.rainbow.shopping.cart.ui.web.utilities.CrudNotificationInfo;
import org.rainbow.shopping.cart.ui.web.utilities.JsfUtil;
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
public class ProductController extends TrackableController<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1423310559956400949L;

	@Inject
	@Qualifier("productService")
	private IService<Product> service;

	@Inject
	private BytesToImageConverter bytesToImageConverter;

	private static final String DUPLICATE_PRODUCT_NUMBER_ERROR_KEY = "DuplicateProductCode";
	private static final String DUPLICATE_PRODUCT_NAME_ERROR_KEY = "DuplicateProductName";

	public ProductController() {
		super(Product.class);
	}

	@Override
	protected IService<Product> getService() {
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
				this.getCurrent().setPhoto(new File());
			File photo = this.getCurrent().getPhoto();
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
