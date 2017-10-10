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
public class FalseImpl<T> extends PredicateImpl implements False<T> {
	private final Expression<T> expression;

	public FalseImpl(Expression<T> expression) {
		this.expression = Objects.requireNonNull(expression);
	}

	@Override
	public Expression<T> getExpression() {
		return expression;
	}
}
