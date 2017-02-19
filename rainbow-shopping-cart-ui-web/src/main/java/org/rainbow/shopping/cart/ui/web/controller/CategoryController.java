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
import org.rainbow.persistence.dao.impl.exceptions.DuplicateCategoryNameException;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.model.Category;
import org.rainbow.shopping.cart.model.Photo;
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
@CrudNotificationInfo(createdMessageKey = "CategoryCreated", updatedMessageKey = "CategoryUpdated", deletedMessageKey = "CategoryDeleted")
public class CategoryController extends TrackableController<Category> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7800239219356574364L;

	@Autowired
	@Qualifier("categoryService")
	private IService<Category> service;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	private static final String DUPLICATE_CATEGORY_NAME_ERROR_KEY = "DuplicateCategoryName";

	private Category parent;

	public CategoryController() {
		super(Category.class);
	}

	@Override
	protected IService<Category> getService() {
		return service;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateCategoryNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateCategoryNameException e = (DuplicateCategoryNameException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_CATEGORY_NAME_ERROR_KEY), e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
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

	public StreamedContent getParentPhoto() throws IOException {
		if (this.getCurrent() == null || this.getCurrent().getParent() == null
				|| this.getCurrent().getParent().getPhoto() == null)
			return new DefaultStreamedContent();
		return bytesToImageConverter.getImage(this.getCurrent().getParent().getPhoto());
	}
}
