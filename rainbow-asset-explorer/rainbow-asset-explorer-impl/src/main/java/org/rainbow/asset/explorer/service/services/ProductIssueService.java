package org.rainbow.asset.explorer.service.services;

import org.rainbow.asset.explorer.orm.entities.ProductIssue;
import org.rainbow.asset.explorer.orm.entities.ProductIssueDetail;
import org.rainbow.persistence.SearchOptions;

public interface ProductIssueService
		extends DocumentDetailsService<ProductIssue, Long, SearchOptions, ProductIssueDetail> {
}