package org.rainbow.criteria;

public interface False<T> extends Predicate {
	Expression<T> getExpression();
}
