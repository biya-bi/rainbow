package org.rainbow.security.core.service;

import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.security.core.entities.Group;

public class GroupService extends RainbowSecurityService<Group, Long, SearchOptions> {

	public GroupService(Dao<Group, Long, SearchOptions> dao) {
		super(dao);
	}
}
