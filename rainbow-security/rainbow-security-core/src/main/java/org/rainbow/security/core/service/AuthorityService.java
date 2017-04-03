package org.rainbow.security.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.security.core.entities.Authority;

public class AuthorityService extends RainbowSecurityService<Authority, Long, SearchOptions> {

	public AuthorityService(Dao<Authority, Long, SearchOptions> dao) {
		super(dao);
	}
}
