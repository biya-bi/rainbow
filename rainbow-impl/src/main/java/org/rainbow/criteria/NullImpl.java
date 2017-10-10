/**
 * 
 */
package org.rainbow.criteria;

import java.util.Objects;

/**
 * @author Biya-Bi
 *
 */
public class NullImpl<T> extends PredicateImpl implements Null<T> {
	private final Expression<T> expression;

	public NullImpl(Expression<T> expression) {
		super();
		this.expression = Objects.requireNonNull(expression);
	}

	@Override
	public Expression<T> getExpression() {
		return expression;
	}
}
