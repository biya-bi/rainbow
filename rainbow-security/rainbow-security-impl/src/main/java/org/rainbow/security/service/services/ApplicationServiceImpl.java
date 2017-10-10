package org.rainbow.security.service.services;

import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.service.exceptions.DuplicateApplicationException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class ApplicationServiceImpl extends ServiceImpl<Application> implements ApplicationService {

	public ApplicationServiceImpl() {
	}

	@Override
	protected void validate(Application application, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", application.getName(), application.getId(), operation)) {
				throw new DuplicateApplicationException(application.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}

}
