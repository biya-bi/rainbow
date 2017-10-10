package org.rainbow.criteria;

public class SearchOptionsFactoryImpl implements SearchOptionsFactory {

	@Override
	public SearchOptions create() {
		return new SearchOptionsImpl();
	}

	@Override
	public SearchOptions create(Predicate predicate) {
		return new SearchOptionsImpl(predicate);
	}

	@Override
	public SearchOptions create(Integer pageIndex, Integer pageSize) {
		return new SearchOptionsImpl(pageIndex, pageSize);
	}

	@Override
	public SearchOptions create(Integer pageIndex, Integer pageSize, Predicate predicate) {
		return new SearchOptionsImpl(pageIndex, pageSize, predicate);
	}

}
