package org.rainbow.security.persistence.listeners;

import org.rainbow.orm.listeners.AbstractAuditableEntityListener;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditableEntityListener extends AbstractAuditableEntityListener {

	@Override
	protected String getUserName() {
		final SecurityContext context = SecurityContextHolder.getContext();
		if (context != null && context.getAuthentication() != null) {
			return context.getAuthentication().getName();
		}
		return null;
	}
}
