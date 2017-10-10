package org.rainbow.criteria;

public class PredicateBuilderFactoryImpl implements PredicateBuilderFactory {

	@Override
	public PredicateBuilder create() {
		return new PredicateBuilderImpl();
	}

}
