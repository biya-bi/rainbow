package org.rainbow.security.service.services;

import java.util.HashMap;
import java.util.Map;

import org.rainbow.persistence.SearchOptions;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.service.exceptions.DuplicateAuthorityException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class AuthorityServiceImpl extends ServiceImpl<Authority, Long, SearchOptions> {

	public AuthorityServiceImpl() {
	}

	@Override
	protected void validate(Authority authority, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> filters = new HashMap<>();
			filters.put("name", authority.getName());
			filters.put("application.id", authority.getApplication().getId());
			if (DaoUtil.isDuplicate(this.getDao(), filters, authority.getId(), operation)) {
				throw new DuplicateAuthorityException(authority.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
