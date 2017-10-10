package org.rainbow.criteria;

import java.util.List;

public interface Predicate extends Expression<Boolean> {

	BooleanOperator getOperator();

	boolean isNegated();

	List<Expression<Boolean>> getExpressions();

	Predicate not();

}
