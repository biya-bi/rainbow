package org.rainbow.security.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.security.core.entities.Application;

public class ApplicationService extends RainbowSecurityService<Application, Long, SearchOptions> {

	public ApplicationService(Dao<Application, Long, SearchOptions> dao) {
		super(dao);
	}
}
