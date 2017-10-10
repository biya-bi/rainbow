/**
 * 
 */
package org.rainbow.criteria;

import java.util.Collection;
import java.util.Objects;

import org.rainbow.criteria.Expression;
import org.rainbow.criteria.In;

/**
 * @author Biya-Bi
 *
 */
public class InImpl<T extends Collection<?>> extends PredicateImpl implements In<T> {
	private final Expression<?> expression;
	private final T value;

	public InImpl(Expression<?> expression, T value) {
		this.expression = Objects.requireNonNull(expression);
		this.value = Objects.requireNonNull(value);
	}

	@Override
	public Expression<?> getExpression() {
		return expression;
	}

	@Override
	public T getValue() {
		return value;
	}

}
