package org.rainbow.security.auth.faces.controllers;

import static org.rainbow.security.auth.faces.utilities.ResourceBundles.MESSAGES;

import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.security.auth.faces.utilities.SessionUtil;
import org.rainbow.security.service.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@Named
@SessionScope
public class AuthenticationController extends AbstractUserNameController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3029122158520546349L;

	private static final String AUTHENTICATION_FAILED_KEY = "AuthenticationFailed";

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("rememberMeServices")
	private RememberMeServices rememberMeServices;

	@Autowired
	@Qualifier("userDetailsService")
	private UserDetailsService userDetailsService;

	@Value("${remember.me.key}")
	private String rememberMeKey;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	@Autowired
	@Qualifier("userAccountService")
	private UserAccountService userAccountService;

	private Date lastLoginDate;
	private String password;
	private String rememberMe;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(String rememberMe) {
		this.rememberMe = rememberMe;
	}

	public boolean isAuthenticated() {
		return SessionUtil.isAuthenticated();
	}

	public Date getLastLoginDate() {
		// This is a org.springframework.web.context.annotation.SessionScope
		// bean. Therefore, we can set the lastLoginDate only once per session.
		if (lastLoginDate == null) {
			lastLoginDate = userAccountService.getLastLoginDate(this.getUserName());
		}
		return lastLoginDate;
	}

	public String login() {
		Exception exception = null;
		String outcome;
		try {
			Authentication result = null;
			if ("TRUE".equalsIgnoreCase(this.getRememberMe())) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserName());
				RememberMeAuthenticationToken rememberMeAuthenticationToken = new RememberMeAuthenticationToken(
						rememberMeKey, userDetails, userDetails.getAuthorities());
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

			outcome = SUCCESS;
		} catch (CredentialsExpiredException e) {
			exception = e;
			outcome = "credentials_expired";
		} catch (Exception e) {
			exception = e;
			outcome = FAILURE;
		}
		if (exception != null) {
			FacesContextUtil.addMessage(FacesMessage.SEVERITY_WARN,
					authenticationExceptionHandler.handle(this.getClass(), exception),
					ResourceBundle.getBundle(MESSAGES).getString(AUTHENTICATION_FAILED_KEY));
		}
		return outcome;
	}

	public AuthenticationManager getAuthenticationManager() {
		return authenticationManager;
	}

	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	public String logout() throws ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest();
		HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
				.getExternalContext().getResponse();

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();
			securityContextLogoutHandler.setInvalidateHttpSession(true);
			securityContextLogoutHandler.setClearAuthentication(true);
			securityContextLogoutHandler.logout(httpServletRequest, httpServletResponse, authentication);
		}

		httpServletRequest.logout();

		return SUCCESS;
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

	public boolean isPasswordExpired() {
		return userAccountService.isPasswordExpired(this.getUserName());
	}

}