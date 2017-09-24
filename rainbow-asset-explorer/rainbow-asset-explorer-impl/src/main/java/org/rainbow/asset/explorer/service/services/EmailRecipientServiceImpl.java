package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.service.exceptions.DuplicateEmailRecipientEmailException;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.ServiceImpl;
import org.rainbow.service.UpdateOperation;
import org.rainbow.utilities.DaoUtil;

public class EmailRecipientServiceImpl extends ServiceImpl<EmailRecipient, Integer, SearchOptions> {

	public EmailRecipientServiceImpl() {
	}

	@Override
	protected void validate(EmailRecipient emailRecipient, UpdateOperation operation) throws Exception {
		switch (operation) {
		case CREATE:
		case UPDATE:
			if (DaoUtil.isDuplicate(this.getDao(), "email", emailRecipient.getEmail(), emailRecipient.getId(),
					operation)) {
				throw new DuplicateEmailRecipientEmailException(emailRecipient.getEmail());
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
	}
}
