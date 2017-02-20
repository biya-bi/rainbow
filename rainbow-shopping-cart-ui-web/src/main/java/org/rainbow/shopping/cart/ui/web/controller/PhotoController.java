package org.rainbow.shopping.cart.ui.web.controller;

import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.model.Category;
import org.rainbow.shopping.cart.model.Photo;
import org.rainbow.shopping.cart.model.Product;
import org.rainbow.shopping.cart.ui.web.utilities.BytesToImageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Named
@RequestScoped
public class PhotoController {

	@Autowired
	@Qualifier("productService")
	private IService<Product> productService;

	@Autowired
	@Qualifier("categoryService")
	private IService<Category> categoryService;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	private StreamedContent getPhoto(String paramName) throws NumberFormatException, Exception {
		if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE)
			return new DefaultStreamedContent();

		Photo photo = null;
		String id = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get(paramName);
		if (paramName == "categoryId") {
			Category category = categoryService.findById(Long.valueOf(id));
			if (category != null && category.getPhoto() != null)
				photo = category.getPhoto();
		} else if (paramName == "productId") {
			Product product = productService.findById(Long.valueOf(id));
			if (product != null && product.getPhoto() != null)
				photo = product.getPhoto();
		}
		if (photo != null)
			return bytesToImageConverter.getImage(photo);
		return new DefaultStreamedContent();
	}

	public StreamedContent getCategoryPhoto() throws NumberFormatException, Exception {
		return getPhoto("categoryId");
	}

	public StreamedContent getProductPhoto() throws NumberFormatException, Exception {
		return getPhoto("productId");
	}
}
