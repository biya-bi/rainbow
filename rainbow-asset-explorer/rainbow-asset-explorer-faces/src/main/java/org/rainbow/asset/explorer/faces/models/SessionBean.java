/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.models;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Biya-Bi
 */

public class SessionBean {

	public static HttpSession getSession() {
		final FacesContext currentInstance = FacesContext.getCurrentInstance();
		if (currentInstance != null && currentInstance.getExternalContext() != null)
			return (HttpSession) currentInstance.getExternalContext().getSession(false);
		return null;
	}

	public static HttpServletRequest getRequest() {
		final FacesContext currentInstance = FacesContext.getCurrentInstance();
		if (currentInstance != null && currentInstance.getExternalContext() != null)
			return (HttpServletRequest) currentInstance.getExternalContext().getRequest();
		return null;
	}

	public static Authentication getAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			return context.getAuthentication();
		}
		return null;
	}

	public static String getUserName() {
		final Authentication authentication = getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		}
		return null;
	}

	public static String getUserId() {
		HttpSession session = getSession();
		if (session != null)
			return (String) session.getAttribute("userid");
		else
			return null;
	}

	public static boolean isAuthenticated() {
		final Authentication authentication = getAuthentication();
		if (authentication != null) {
			return authentication.isAuthenticated() &&
			// when Anonymous Authentication is enabled
					!(authentication instanceof AnonymousAuthenticationToken);
		}
		return false;
	}

}
