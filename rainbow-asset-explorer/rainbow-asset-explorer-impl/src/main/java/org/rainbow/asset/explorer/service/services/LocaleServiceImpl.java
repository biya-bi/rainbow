package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocaleLcidException;
import org.rainbow.asset.explorer.service.exceptions.DuplicateLocaleNameException;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class LocaleServiceImpl extends ServiceImpl<Locale> implements LocaleService {

	public LocaleServiceImpl() {
	}

	@Override
	protected void validate(Locale locale, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", locale.getName(), locale.getId(), operation)) {
				throw new DuplicateLocaleNameException(locale.getName());
			}
			if (DaoUtil.isDuplicate(this.getDao(), "lcid", locale.getLcid(), locale.getId(), operation)) {
				throw new DuplicateLocaleLcidException(locale.getLcid());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
