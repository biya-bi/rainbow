package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.rainbow.asset.explorer.faces.models.LoginInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

@Component
@Named
@RequestScoped
public class AuthenticationController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2979094061352820116L;

	private static final String AUTHENTICATION_FAILED_KEY = "AuthenticationFailed";

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("rememberMeServices")
	private RememberMeServices rememberMeServices;

	@Autowired
	@Qualifier("jdbcUserDetailsService")
	private UserDetailsService userDetailsService;

	@Value("${remember.me.key}")
	private String rememberMeKey;

	@Autowired
	private AuthenticationExceptionHandler authenticationExceptionHandler;

	private boolean passwordExpired = false;
	private final LoginInfo loginInfo = new LoginInfo();
	private Date lastLoginDate;

	/**
	 * Login.
	 * 
	 * @return the string
	 * @throws IOException
	 */
	public String login() throws IOException {
		try {
			Authentication result = null;
			if ("TRUE".equalsIgnoreCase(this.loginInfo.getRememberMe())) {
				UserDetails userDetails = userDetailsService.loadUserByUsername(this.loginInfo.getUserName());
				RememberMeAuthenticationToken rememberMeAuthenticationToken = new RememberMeAuthenticationToken(
						rememberMeKey, userDetails, userDetails.getAuthorities());
				HttpServletRequest httpServletRequest = (HttpServletRequest) FacesContext.getCurrentInstance()
						.getExternalContext().getRequest();
				HttpServletResponse httpServletResponse = (HttpServletResponse) FacesContext.getCurrentInstance()
						.getExternalContext().getResponse();
				rememberMeServices.loginSuccess(httpServletRequest, httpServletResponse, rememberMeAuthenticationToken);
				result = rememberMeAuthenticationToken;
			} else {
				Authentication request = new UsernamePasswordAuthenticationToken(this.loginInfo.getUserName(),
						this.loginInfo.getPassword());
				result = authenticationManager.authenticate(request);
			}
			SecurityContextHolder.getContext().setAuthentication(result);

			lastLoginDate = new Date();

			return "success";
		}

		catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							authenticationExceptionHandler.handle(this.getClass(), e),
							ResourceBundle.getBundle(SECURITY_MESSAGES).getString(AUTHENTICATION_FAILED_KEY)));

			return "failure";
		}
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

		return "success";
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
		return passwordExpired;
	}

	public LoginInfo getLoginInfo() {
		return loginInfo;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

}