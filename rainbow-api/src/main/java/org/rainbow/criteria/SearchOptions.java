package org.rainbow.criteria;

public interface SearchOptions {
	Integer getPageIndex();

	Integer getPageSize();

	Predicate getPredicate();
}
