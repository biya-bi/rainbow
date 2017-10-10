package org.rainbow.criteria;

import java.util.Collection;

public interface In<T extends Collection<?>> extends Predicate {
	Expression<?> getExpression();

	T getValue();
}
