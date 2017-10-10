package org.rainbow.criteria;

import java.util.Collection;

public interface PredicateBuilder {

	Predicate and(Expression<Boolean> x, Expression<Boolean> y);

	Predicate and(Predicate... restrictions);

	Predicate or(Expression<Boolean> x, Expression<Boolean> y);

	Predicate or(Predicate... restrictions);

	Predicate not(Expression<Boolean> restriction);

	Predicate isTrue(Expression<Boolean> x);

	Predicate isFalse(Expression<Boolean> x);

	Predicate isNull(Expression<?> x);

	Predicate isNotNull(Expression<?> x);

	Predicate equal(Expression<?> x, Expression<?> y);

	Predicate equal(Expression<?> x, Object y);

	Predicate notEqual(Expression<?> x, Expression<?> y);

	Predicate notEqual(Expression<?> x, Object y);

	<Y extends Comparable<? super Y>> Predicate greaterThan(Expression<?> x, Expression<?> y);

	<Y extends Comparable<? super Y>> Predicate greaterThan(Expression<?> x, Y y);

	<Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<?> x, Expression<?> y);

	<Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<?> x, Y y);

	<Y extends Comparable<? super Y>> Predicate lessThan(Expression<?> x, Expression<?> y);

	<Y extends Comparable<? super Y>> Predicate lessThan(Expression<?> x, Y y);

	<Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<?> x, Expression<?> y);

	<Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<?> x, Y y);

	<Y extends Comparable<? super Y>> Predicate between(Expression<?> v, Expression<?> x, Expression<?> y);

	<Y extends Comparable<? super Y>> Predicate between(Expression<?> v, Y x, Y y);

	Predicate contains(Expression<String> x, String pattern);

	Predicate startsWith(Expression<String> x, String pattern);

	Predicate endsWith(Expression<String> x, String pattern);

	<T extends Collection<?>> In<T> in(Expression<?> expression, T value);

}
