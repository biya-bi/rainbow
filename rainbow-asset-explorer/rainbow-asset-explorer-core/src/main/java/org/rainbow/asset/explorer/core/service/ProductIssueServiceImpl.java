package org.rainbow.asset.explorer.core.service;

import java.util.List;

import org.rainbow.asset.explorer.core.entities.ProductIssue;
import org.rainbow.asset.explorer.core.entities.ProductIssueDetail;
import org.rainbow.asset.explorer.core.persistence.dao.ProductIssueDao;
import org.rainbow.core.persistence.SearchOptions;

public class ProductIssueServiceImpl extends RainbowAssetExplorerServiceImpl<ProductIssue, Long, SearchOptions>
		implements ProductIssueService {

	public ProductIssueServiceImpl(ProductIssueDao dao) {
		super(dao);
	}

	@Override
	public List<ProductIssueDetail> getDetails(Long productIssueId) {
		return ((ProductIssueDao) this.getDao()).getDetails(productIssueId);
	}
}
