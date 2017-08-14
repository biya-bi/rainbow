package org.rainbow.asset.explorer.core.service;

import java.util.List;

import org.rainbow.asset.explorer.core.entities.ProductIssue;
import org.rainbow.asset.explorer.core.entities.ProductIssueDetail;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;

public interface ProductIssueService extends Service<ProductIssue, Long, SearchOptions>{

	List<ProductIssueDetail> getDetails(Long productIssueId);

}