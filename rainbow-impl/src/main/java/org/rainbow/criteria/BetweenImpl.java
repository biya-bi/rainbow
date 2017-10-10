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
public class BetweenImpl extends PredicateImpl implements Between {
	private final Expression<?> value;
	private final Expression<?> lowerBound;
	private final Expression<?> upperBound;

	public BetweenImpl(Expression<?> v, Expression<?> lowerBound, Expression<?> upperBound) {
		this.value = Objects.requireNonNull(v);
		this.lowerBound = Objects.requireNonNull(lowerBound);
		this.upperBound = Objects.requireNonNull(upperBound);
	}

	@Override
	public Expression<?> getValue() {
		return value;
	}

	@Override
	public Expression<?> getLowerBound() {
		return lowerBound;
	}

	@Override
	public Expression<?> getUpperBound() {
		return upperBound;
	}

}
