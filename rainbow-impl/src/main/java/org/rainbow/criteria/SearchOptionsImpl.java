package org.rainbow.criteria;

import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.SearchOptions;

public class SearchOptionsImpl implements SearchOptions {
	private Integer pageIndex;
	private Integer pageSize;
	private Predicate predicate;

	public SearchOptionsImpl(Integer pageIndex, Integer pageSize, Predicate predicate) {
		super();
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		this.predicate = predicate;
	}

	public SearchOptionsImpl() {
		this(null, null, null);
	}

	public SearchOptionsImpl(Integer pageIndex, Integer pageSize) {
		this(pageIndex, pageSize, null);
	}

	public SearchOptionsImpl(Predicate predicate) {
		this(null, null, predicate);
	}

	@Override
	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	@Override
	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public Predicate getPredicate() {
		return predicate;
	}

	public void setPredicate(Predicate predicate) {
		this.predicate = predicate;
	}

}
