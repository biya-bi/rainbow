package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.service.exceptions.DuplicateEmailTemplateNameException;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class EmailTemplateServiceImpl extends ServiceImpl<EmailTemplate, Integer, SearchOptions> {

	public EmailTemplateServiceImpl() {
	}

	@Override
	protected void validate(EmailTemplate emailTemplate, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "name", emailTemplate.getName(), emailTemplate.getId(),
					operation)) {
				throw new DuplicateEmailTemplateNameException(emailTemplate.getName());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
