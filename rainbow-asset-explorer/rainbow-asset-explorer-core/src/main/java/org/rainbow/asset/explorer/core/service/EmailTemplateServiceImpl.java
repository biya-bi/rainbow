package org.rainbow.asset.explorer.core.service;

import org.rainbow.asset.explorer.core.entities.EmailTemplate;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public class EmailTemplateServiceImpl extends RainbowAssetExplorerServiceImpl<EmailTemplate, Integer, SearchOptions> {

	public EmailTemplateServiceImpl(Dao<EmailTemplate, Integer, SearchOptions> dao) {
		super(dao);
	}
}
