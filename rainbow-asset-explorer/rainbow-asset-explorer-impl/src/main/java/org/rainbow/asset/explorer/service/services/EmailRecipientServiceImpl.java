package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.service.exceptions.DuplicateEmailRecipientEmailException;
import org.rainbow.service.services.ServiceImpl;
import org.rainbow.service.services.UpdateOperation;
import org.rainbow.util.DaoUtil;

public class EmailRecipientServiceImpl extends ServiceImpl<EmailRecipient> implements EmailRecipientService {

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
