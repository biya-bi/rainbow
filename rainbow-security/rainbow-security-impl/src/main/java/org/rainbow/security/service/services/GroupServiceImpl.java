package org.rainbow.security.service.services;

import java.util.HashMap;
import java.util.Map;

import org.rainbow.persistence.SearchOptions;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.exceptions.DuplicateGroupException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class GroupServiceImpl extends ServiceImpl<Group, Long, SearchOptions> {

	public GroupServiceImpl() {
	}

	@Override
	protected void validate(Group group, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> filters = new HashMap<>();
			filters.put("name", group.getName());
			filters.put("application.id", group.getApplication().getId());
			if (DaoUtil.isDuplicate(this.getDao(), filters, group.getId(), operation)) {
				throw new DuplicateGroupException(group.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

}
