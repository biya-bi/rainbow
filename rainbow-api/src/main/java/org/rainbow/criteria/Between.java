package org.rainbow.criteria;

public interface Between extends Predicate {
	Expression<?> getValue();

	Expression<?> getLowerBound();

	Expression<?> getUpperBound();
}
