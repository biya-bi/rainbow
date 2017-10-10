package org.rainbow.criteria;

import java.util.Objects;

public class StringExpressionImpl extends PredicateImpl implements StringExpression {
	private final Expression<String> expression;
	private final StringOperator stringOperator;
	private final String pattern;

	public StringExpressionImpl(Expression<String> expression, StringOperator stringOperator, String pattern) {
		this.expression = Objects.requireNonNull(expression);
		this.stringOperator = Objects.requireNonNull(stringOperator);
		this.pattern = Objects.requireNonNull(pattern);
	}

	@Override
	public Expression<String> getExpression() {
		return expression;
	}

	@Override
	public StringOperator getStringOperator() {
		return stringOperator;
	}

	@Override
	public String getPattern() {
		return pattern;
	}

}
