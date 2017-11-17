package org.rainbow.journal.service.services;

import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.service.exceptions.DuplicateProfileException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class ProfileServiceImpl extends ServiceImpl<Profile> implements ProfileService {

	public ProfileServiceImpl() {
	}

	@Override
	protected void validate(Profile profile, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "userName", profile.getUserName(), profile.getId(), operation)) {
				throw new DuplicateProfileException(profile.getUserName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
