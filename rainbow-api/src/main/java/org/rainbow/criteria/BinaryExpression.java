package org.rainbow.criteria;

public interface BinaryExpression extends Predicate {
	Expression<?> getX();

	Expression<?> getY();

	RelationalOperator getRelationalOperator();
}
