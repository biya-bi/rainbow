package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.Department;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class DepartmentServiceImpl extends RainbowAssetExplorerServiceImpl<Department, Integer, SearchOptions> {

	public DepartmentServiceImpl(Dao<Department, Integer, SearchOptions> dao) {
		super(dao);
	}
}
