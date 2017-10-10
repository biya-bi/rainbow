package org.rainbow.criteria;

import java.util.Arrays;
import java.util.Collection;

public class PredicateBuilderImpl implements PredicateBuilder {

	@Override
	public Predicate and(Expression<Boolean> x, Expression<Boolean> y) {
		return new PredicateImpl(BooleanOperator.AND, Arrays.asList(x, y));
	}

	@Override
	public Predicate and(Predicate... restrictions) {
		return new PredicateImpl(BooleanOperator.AND, Arrays.asList(restrictions));
	}

	@Override
	public Predicate or(Expression<Boolean> x, Expression<Boolean> y) {
		return new PredicateImpl(BooleanOperator.OR, Arrays.asList(x, y));
	}

	@Override
	public Predicate or(Predicate... restrictions) {
		return new PredicateImpl(BooleanOperator.OR, Arrays.asList(restrictions));
	}

	@Override
	public Predicate not(Expression<Boolean> restriction) {
		PredicateImpl p = new PredicateImpl(Arrays.asList(restriction));
		p.setNegated(true);
		return p;
	}

	@Override
	public Predicate isTrue(Expression<Boolean> x) {
		return new TrueImpl<>(x);
	}

	@Override
	public Predicate isFalse(Expression<Boolean> x) {
		return new FalseImpl<>(x);
	}

	@Override
	public Predicate isNull(Expression<?> x) {
		return new NullImpl<>(x);
	}

	@Override
	public Predicate isNotNull(Expression<?> x) {
		NullImpl<?> p = new NullImpl<>(x);
		p.setNegated(true);
		return p;
	}

	@Override
	public Predicate equal(Expression<?> x, Expression<?> y) {
		return new BinaryExpressionImpl(x, RelationalOperator.EQUAL, y);
	}

	@Override
	public Predicate equal(Expression<?> x, Object y) {
		return new BinaryExpressionImpl(x, RelationalOperator.EQUAL, new ValueImpl<>(y));
	}

	@Override
	public Predicate notEqual(Expression<?> x, Expression<?> y) {
		return new BinaryExpressionImpl(x, RelationalOperator.NOT_EQUAL, y);
	}

	@Override
	public Predicate notEqual(Expression<?> x, Object y) {
		return new BinaryExpressionImpl(x, RelationalOperator.NOT_EQUAL, new ValueImpl<>(y));
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<?> x, Expression<?> y) {
		return new BinaryExpressionImpl(x, RelationalOperator.GREATER_THAN, y);
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<?> x, Y y) {
		return new BinaryExpressionImpl(x, RelationalOperator.GREATER_THAN, new ValueImpl<>(y));
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<?> x, Expression<?> y) {
		return new BinaryExpressionImpl(x, RelationalOperator.GREATER_THAN_OR_EQUAL, y);
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<?> x, Y y) {
		return new BinaryExpressionImpl(x, RelationalOperator.GREATER_THAN_OR_EQUAL, new ValueImpl<>(y));
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<?> x, Expression<?> y) {
		return new BinaryExpressionImpl(x, RelationalOperator.LESS_THAN, y);
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<?> x, Y y) {
		return new BinaryExpressionImpl(x, RelationalOperator.LESS_THAN, new ValueImpl<>(y));
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<?> x, Expression<?> y) {
		return new BinaryExpressionImpl(x, RelationalOperator.LESS_THAN_OR_EQUAL, y);
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<?> x, Y y) {
		return new BinaryExpressionImpl(x, RelationalOperator.LESS_THAN_OR_EQUAL, new ValueImpl<>(y));
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate between(Expression<?> v, Expression<?> x, Expression<?> y) {
		return new BetweenImpl(v, x, y);
	}

	@Override
	public <Y extends Comparable<? super Y>> Predicate between(Expression<?> v, Y x, Y y) {
		return new BetweenImpl(v, new ValueImpl<>(x), new ValueImpl<>(y));
	}

	@Override
	public Predicate contains(Expression<String> x, String pattern) {
		return new StringExpressionImpl(x, StringOperator.CONTAINS, pattern);
	}

	@Override
	public Predicate startsWith(Expression<String> x, String pattern) {
		return new StringExpressionImpl(x, StringOperator.STARTS_WITH, pattern);
	}

	@Override
	public Predicate endsWith(Expression<String> x, String pattern) {
		return new StringExpressionImpl(x, StringOperator.ENDS_WITH, pattern);
	}

	@Override
	public <T extends Collection<?>> In<T> in(Expression<?> expression, T value) {
		return new InImpl<>(expression, value);
	}

}
