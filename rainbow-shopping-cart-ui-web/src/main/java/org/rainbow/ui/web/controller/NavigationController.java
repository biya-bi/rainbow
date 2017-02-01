/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.ui.web.controller;

import java.io.IOException;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Biya-Bi
 */
@Named
@ViewScoped
public class NavigationController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8127436342159225288L;
	private String selectedUrl;
	private final String SELECTED_PANE_COOKIE_NAME = "RAINBOW_SHOPPING_CART_STOREFRONT_INDEX_SELECTED_LIST";

	@PostConstruct
	private void init() {
		// Cookie[] cookies = ((HttpServletRequest)
		// FacesContext.getCurrentInstance().getExternalContext().getRequest()).getCookies();

		FacesContext instance = FacesContext.getCurrentInstance();
		if (instance != null) {
			ExternalContext context = instance.getExternalContext();
			if (context != null) {
				Object request = context.getRequest();
				if (request != null) {
					Cookie[] cookies = ((HttpServletRequest) request).getCookies();

					if (cookies != null) {
						for (Cookie cookie : cookies) {
							if (cookie.getName().equals(SELECTED_PANE_COOKIE_NAME)) {
								selectedUrl = cookie.getValue();
							}
						}
					}
				}
			}
		}
	}

	public String getSelectedUrl() {

		if (selectedUrl == null) {
			selectedUrl = "/WEB-INF/pages/home.xhtml";
		}

		return selectedUrl;
	}

	public void setSelectedUrl(String selectedUrl) throws IOException {
		this.selectedUrl = selectedUrl;

		if (this.selectedUrl != null) {

			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

			Cookie cookie = new Cookie(SELECTED_PANE_COOKIE_NAME, this.selectedUrl);
			cookie.setPath(request.getContextPath());
			cookie.setHttpOnly(true);

			response.addCookie(cookie);
		}
	}

}
