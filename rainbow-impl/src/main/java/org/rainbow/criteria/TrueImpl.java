/**
 * 
 */
package org.rainbow.criteria;

import java.util.Objects;

import org.rainbow.criteria.Expression;

/**
 * @author Biya-Bi
 *
 */
public class TrueImpl<T> extends PredicateImpl implements True<T> {
	private final Expression<T> expression;

	public TrueImpl(Expression<T> expression) {
		this.expression = Objects.requireNonNull(expression);
	}

	@Override
	public Expression<T> getExpression() {
		return expression;
	}
}
