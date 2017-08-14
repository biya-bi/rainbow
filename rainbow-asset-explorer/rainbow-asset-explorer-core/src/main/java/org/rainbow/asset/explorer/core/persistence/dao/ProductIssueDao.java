package org.rainbow.asset.explorer.core.persistence.dao;

import java.util.List;

import org.rainbow.asset.explorer.core.entities.ProductIssue;
import org.rainbow.asset.explorer.core.entities.ProductIssueDetail;
import org.rainbow.core.persistence.Dao;
import org.rainbow.core.persistence.SearchOptions;

public interface ProductIssueDao extends Dao<ProductIssue, Long, SearchOptions>{

	List<ProductIssueDetail> getDetails(Long productIssueId);

}