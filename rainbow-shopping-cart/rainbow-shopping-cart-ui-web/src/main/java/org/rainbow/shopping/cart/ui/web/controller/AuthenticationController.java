package org.rainbow.shopping.cart.ui.web.controller;

import static org.rainbow.shopping.cart.ui.web.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.io.IOException;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.stereotype.Component;

/**
 * The Class AuthenticationController.
 */
@Component
@Named
@RequestScoped
public class AuthenticationController {

	private String userName;

	private String password;

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	private String rememberMe;

	@Autowired
	@Qualifier("rememberMeServices")
	private RememberMeServices rememberMeServices;

	@Autowired
	@Qualifier("jdbcUserDetailsService")
	private UserDetailsService userDetailsService;

	private static final String AUTHENTICATION_FAILED_KEY = "AuthenticationFailed";
	private static final String INVALID_CREDENTIALS_KEY = "InvalidCredentials";
	private static final String CREDENTIALS_EXPIRED_KEY = "UserCredentialsExpiredPersonal";
	private static final String USER_LOCKED_OUT_KEY = "UserLockedOutPersonal";
	private static final String USER_DISABLED_KEY = "UserDisabledPersonal";

	private boolean hasRole(String role) {
		@SuppressWarnings("unchecked")
		Collection<GrantedAuthority> authorities = (Collection<GrantedAuthority>) SecurityContextHolder.getContext()
				.getAuthentication().getAuthorities();
		boolean hasRole = false;
		for (GrantedAuthority authority : authorities) {
			hasRole = authority.getAuthority().equals(role);
			if (hasRole) {
				break;
			}
		}
		return hasRole;
	}

	/**
	 * Login.
	 * 
	 * @return the string
	 * @throws IOException
	 */
	public String login() throws IOException {
		String msg = null;
		try {
			Authentication result = null;
			if ("TRUE".equalsIgnoreCase(this.getRememberMe())) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(getUserName());
				RememberMeAuthenticationToken rememberMeAuthenticationToken = new RememberMeAuthenticationToken(
						"rainbow-shopping-cart-remember-me", userDetails, userDetails.getAuthorities());
				HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
						.getExternalContext().getRequest();
				HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
						.getExternalContext().getResponse();
				rememberMeServices.loginSuccess(httpServletRequest, httpServletResponse, rememberMeAuthenticationToken);
				result = rememberMeAuthenticationToken;
			} else {
				Authentication request = new UsernamePasswordAuthenticationToken(this.getUserName(),
						this.getPassword());
				result = authenticationManager.authenticate(request);
			}
			SecurityContextHolder.getContext().setAuthentication(result);

			if (hasRole("ROLE_STORE_ADMIN"))
				return "admin";
			else
				return "web_user";
		} catch (BadCredentialsException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, e);
			msg = ResourceBundle.getBundle(SECURITY_MESSAGES).getString(INVALID_CREDENTIALS_KEY);
		} catch (LockedException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, e);
			msg = ResourceBundle.getBundle(SECURITY_MESSAGES).getString(USER_LOCKED_OUT_KEY);
		} catch (DisabledException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, e);
			msg = ResourceBundle.getBundle(SECURITY_MESSAGES).getString(USER_DISABLED_KEY);
		} catch (CredentialsExpiredException e) {
			Logger.getLogger(this.getClass().getName()).log(Level.WARNING, null, e);
			msg = ResourceBundle.getBundle(SECURITY_MESSAGES).getString(CREDENTIALS_EXPIRED_KEY);
		}
		if (msg != null) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, msg,
					ResourceBundle.getBundle(SECURITY_MESSAGES).getString(AUTHENTICATION_FAILED_KEY)));
		}
		return null;
	}

	/**
	 * Cancel.
	 * 
	 * @return the string
	 */
	public String cancel() {
		return null;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	/**
	 * Gets the user name.
	 *
	 * @return the user name
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the user name.
	 * 
	 * @param userName
	 *            the new user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public String logout() {
		SecurityContextHolder.clearContext();
		/**
		 * Delete Cookies
		 */
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
				.getExternalContext().getResponse();
		Cookie cookie = new Cookie("SPRING_SECURITY_REMEMBER_ME_COOKIE", null);
		cookie.setMaxAge(0);
		cookie.setPath(httpServletRequest.getContextPath().length() > 0 ? httpServletRequest.getContextPath() : "/");
		httpServletResponse.addCookie(cookie);
		return "loggedout";
	}

	/**
	 * Gets the remember me.
	 *
	 * @return the remember me
	 */
	public String getRememberMe() {
		return rememberMe;
	}

	/**
	 * Sets the remember me.
	 *
	 * @param rememberMe
	 *            the new remember me
	 */
	public void setRememberMe(String rememberMe) {
		this.rememberMe = rememberMe;
	}

	/**
	 * Gets the remember me services.
	 *
	 * @return the remember me services
	 */
	public RememberMeServices getRememberMeServices() {
		return rememberMeServices;
	}

	/**
	 * Sets the remember me services.
	 *
	 * @param rememberMeServices
	 *            the new remember me services
	 */
	public void setRememberMeServices(RememberMeServices rememberMeServices) {
		this.rememberMeServices = rememberMeServices;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
}