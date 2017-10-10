package org.rainbow.criteria;

public interface True<T> extends Predicate {
	Expression<T> getExpression();
}
