package org.rainbow.criteria;

public interface StringExpression extends Predicate {
	Expression<String> getExpression();

	StringOperator getStringOperator();

	String getPattern();
}
