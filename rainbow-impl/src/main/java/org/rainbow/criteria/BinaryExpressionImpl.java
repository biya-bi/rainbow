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
public class BinaryExpressionImpl extends PredicateImpl implements BinaryExpression {
	private final Expression<?> x;
	private final RelationalOperator operator;
	private final Expression<?> y;

	public BinaryExpressionImpl(Expression<?> x, RelationalOperator relationalOperator, Expression<?> y) {
		this.x = Objects.requireNonNull(x);
		this.operator = Objects.requireNonNull(relationalOperator);
		this.y = Objects.requireNonNull(y);
	}

	@Override
	public Expression<?> getX() {
		return x;
	}

	@Override
	public Expression<?> getY() {
		return y;
	}

	@Override
	public RelationalOperator getRelationalOperator() {
		return operator;
	}

}
