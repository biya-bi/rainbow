package org.rainbow.criteria;

public interface Null<T> extends Predicate {
	Expression<T> getExpression();
}
