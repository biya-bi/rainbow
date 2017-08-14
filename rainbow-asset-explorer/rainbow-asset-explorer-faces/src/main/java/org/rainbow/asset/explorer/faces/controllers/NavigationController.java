/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class NavigationController implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 8127436342159225288L;
	private String selectedUrl;

	@Value("${navigation.selected.url.key}")
	private String navigationSelectedUrlKey;

	@PostConstruct
	private void init() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext != null) {
			ExternalContext externalContext = facesContext.getExternalContext();
			if (externalContext != null && (externalContext.getRequest() instanceof HttpServletRequest)) {
				Cookie[] cookies = ((HttpServletRequest) externalContext.getRequest()).getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if (cookie.getName().equals(navigationSelectedUrlKey)) {
							selectedUrl = cookie.getValue();
						}
					}
				}
			}
		}
	}

	public String getSelectedUrl() {

		if (selectedUrl == null) {
			selectedUrl = "/WEB-INF/includes/home.xhtml";
		}

		return selectedUrl;
	}

	public void setSelectedUrl(String selectedUrl) {
		this.selectedUrl = selectedUrl;
	}

	public void navigate() {

		if (this.selectedUrl != null) {
			ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();

			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

			if (!this.selectedUrl.toLowerCase().contains("errors/unexpectederror")) {
				Cookie cookie = new Cookie(navigationSelectedUrlKey, this.selectedUrl);
				cookie.setPath(request.getContextPath());
				cookie.setHttpOnly(true);

				response.addCookie(cookie);
			}
		}
	}

}
