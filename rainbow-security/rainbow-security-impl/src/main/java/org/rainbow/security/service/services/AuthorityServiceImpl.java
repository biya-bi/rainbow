package org.rainbow.security.service.services;

import java.util.HashMap;
import java.util.Map;

import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.service.exceptions.DuplicateAuthorityException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class AuthorityServiceImpl extends ServiceImpl<Authority> implements AuthorityService {

	public AuthorityServiceImpl() {
	}

	@Override
	protected void validate(Authority authority, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> pathValuePairs = new HashMap<>();
			pathValuePairs.put("name", authority.getName());
			pathValuePairs.put("application.id", authority.getApplication().getId());
			if (DaoUtil.isDuplicate(this.getDao(), pathValuePairs, authority.getId(), operation)) {
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
