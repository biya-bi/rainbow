package org.rainbow.security.service.services;

import java.util.HashMap;
import java.util.Map;

import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.exceptions.DuplicateGroupException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class GroupServiceImpl extends ServiceImpl<Group> implements GroupService {

	public GroupServiceImpl() {
	}

	@Override
	protected void validate(Group group, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			final Map<String, Comparable<?>> pathValuePairs = new HashMap<>();
			pathValuePairs.put("name", group.getName());
			pathValuePairs.put("application.id", group.getApplication().getId());
			if (DaoUtil.isDuplicate(this.getDao(), pathValuePairs, group.getId(), operation)) {
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
