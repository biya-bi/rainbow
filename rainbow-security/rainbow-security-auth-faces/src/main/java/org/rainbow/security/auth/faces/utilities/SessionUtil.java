package org.rainbow.security.auth.faces.utilities;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 *
 * @author Biya-Bi
 */

public class SessionUtil {

	public static Authentication getAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			return context.getAuthentication();
		}
		return null;
	}

	/**
	 * Returns the user name of the currently authenticated non-anonymous user.
	 * 
	 * @return
	 */
	public static String getUserName() {
		if (isAuthenticated()) {
			return getAuthentication().getName();
		}
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
