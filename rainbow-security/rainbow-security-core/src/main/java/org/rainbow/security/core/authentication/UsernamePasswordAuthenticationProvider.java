package org.rainbow.security.core.authentication;

import org.rainbow.security.core.utilities.TransactionSettings;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

/**
 * An {@link org.springframework.security.authentication.AuthenticationProvider}
 * implementation that is designed to respond to
 * {@link org.springframework.security.authentication.UsernamePasswordAuthenticationToken}
 * authentication requests.
 * 
 * @author Biya-Bi
 *
 */
@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, noRollbackFor = AuthenticationException.class)
public class UsernamePasswordAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private UserDetailsService userDetailsService;
	private PasswordEncoder passwordEncoder;
	private String applicationName;
	private PasswordAttemptUpdater passwordAttemptUpdater;

	public UsernamePasswordAuthenticationProvider(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder, PasswordAttemptUpdater passwordAttemptUpdater, String applicationName) {
		super();
		if (userDetailsService == null)
			throw new IllegalArgumentException("The userDetailsService argument cannot be null.");
		if (passwordEncoder == null)
			throw new IllegalArgumentException("The passwordEncoder argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		if (passwordAttemptUpdater == null)
			throw new IllegalArgumentException("The passwordAttemptUpdater argument cannot be null.");
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.passwordAttemptUpdater = passwordAttemptUpdater;
		this.applicationName = applicationName;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public String getApplicationName() {
		return applicationName;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

		if (authentication.getCredentials() == null) {
			logger.debug("Authentication failed: no credentials provided");

			throw new BadCredentialsException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		String presentedPassword = authentication.getCredentials().toString();

		if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
			logger.debug("Authentication failed: password does not match stored value");

			throw new BadCredentialsException(
					messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {

		passwordAttemptUpdater.update(username, authentication.getCredentials().toString());

		UserDetails loadedUser = userDetailsService.loadUserByUsername(username);
		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}
		return loadedUser;
	}

	@Transactional(transactionManager = TransactionSettings.TRANSACTION_MANAGER_NAME, noRollbackFor = AuthenticationException.class)
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		return super.authenticate(authentication);
	}
}
