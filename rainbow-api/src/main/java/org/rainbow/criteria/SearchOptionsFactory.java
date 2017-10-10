package org.rainbow.criteria;

public interface SearchOptionsFactory {
	SearchOptions create();

	SearchOptions create(Predicate predicate);

	SearchOptions create(Integer pageIndex, Integer pageSize);

	SearchOptions create(Integer pageIndex, Integer pageSize, Predicate predicate);
}
