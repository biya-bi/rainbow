package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Department;
import org.rainbow.asset.explorer.service.exceptions.DuplicateDepartmentNameException;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class DepartmentServiceImpl extends ServiceImpl<Department, Integer, SearchOptions> {

	public DepartmentServiceImpl() {
	}

	@Override
	protected void validate(Department department, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", department.getName(), department.getId(), operation)) {
				throw new DuplicateDepartmentNameException(department.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
