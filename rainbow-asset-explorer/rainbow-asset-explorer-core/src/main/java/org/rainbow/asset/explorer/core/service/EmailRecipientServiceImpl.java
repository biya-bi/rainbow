package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.EmailRecipient;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class EmailRecipientServiceImpl extends RainbowAssetExplorerServiceImpl<EmailRecipient, Integer, SearchOptions> {

	public EmailRecipientServiceImpl(Dao<EmailRecipient, Integer, SearchOptions> dao) {
		super(dao);
	}
}
