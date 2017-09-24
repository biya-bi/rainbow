package org.rainbow.security.service.authentication;

import org.rainbow.security.service.authentication.PasswordAttemptUpdater;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * An {@link org.springframework.security.authentication.AuthenticationProvider}
 * implementation that is designed to respond to
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
 * authentication requests.
 * 
 * @author Biya-Bi
 *
 */
public class UserDetailsAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private UserDetailsService userDetailsService;
	private PasswordEncoder passwordEncoder;
	private PasswordAttemptUpdater passwordAttemptUpdater;

	public UserDetailsAuthenticationProvider() {
		super();
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public PasswordAttemptUpdater getPasswordAttemptUpdater() {
		return passwordAttemptUpdater;
	}

	public void setPasswordAttemptUpdater(PasswordAttemptUpdater passwordAttemptUpdater) {
		this.passwordAttemptUpdater = passwordAttemptUpdater;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		checkDependencies();
		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!this.getPasswordEncoder().matches(presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		checkDependencies();
		this.getPasswordAttemptUpdater().update(username, authentication.getCredentials().toString());
		final UserDetails userDetails = this.getUserDetailsService().loadUserByUsername(username);
		if (userDetails == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}
		return userDetails;
	}

	private void checkDependencies() {
		if (this.getUserDetailsService() == null) {
			throw new IllegalStateException("The user details service cannot be null.");
		}
		if (this.getPasswordEncoder() == null) {
			throw new IllegalStateException("The password encoder cannot be null.");
		}
		if (this.getPasswordAttemptUpdater() == null) {
			throw new IllegalStateException("The password attempt updater cannot be null.");
		}
	}
}
