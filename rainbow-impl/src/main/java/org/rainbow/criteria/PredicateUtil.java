package org.rainbow.criteria;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PredicateUtil {
	public static <T> Predicate map(org.rainbow.criteria.Predicate predicate, CriteriaBuilder cb, Root<T> rt) {
		Objects.requireNonNull(predicate);
		Objects.requireNonNull(cb);
		Objects.requireNonNull(rt);

		Expression<?> p = getExpression(predicate, cb, rt);
		if (!(p instanceof Predicate)) {
			throw new IllegalStateException(
					String.format("The %s corresponding to the specified %s cannot be converted to a %s",
							Expression.class.getName(), Predicate.class.getName(), Predicate.class.getName()));
		}
		return (Predicate) p;
	}

	private static <T> Path<?> getPath(org.rainbow.criteria.Path path, Root<T> rt) {
		return getPath(path.get(), rt);
	}

	private static <T> Predicate getOrNegate(Predicate predicate, boolean negate, CriteriaBuilder cb) {
		return !negate ? predicate : cb.not(predicate);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> Expression<?> getExpression(org.rainbow.criteria.Expression<?> exp, CriteriaBuilder cb,
			Root<T> rt) {
		if (exp instanceof org.rainbow.criteria.Path) {
			return getPath((org.rainbow.criteria.Path) exp, rt);
		} else if (exp instanceof Value) {
			return cb.literal(((Value<?>) exp).get());
		} else if (exp instanceof Null) {
			Null<?> nullableExpression = (Null<?>) exp;
			org.rainbow.criteria.Expression<?> e = nullableExpression.getExpression();
			if (e instanceof org.rainbow.criteria.Path) {
				return getOrNegate(cb.isNull(getPath((org.rainbow.criteria.Path) e, rt)),
						nullableExpression.isNegated(), cb);
			}
		} else if (exp instanceof In) {
			In<?> inExpression = (In<?>) exp;
			Expression<?> e = getExpression(inExpression.getExpression(), cb, rt);
			Collection<?> value = inExpression.getValue();
			Predicate p = e.in(value);
			return getOrNegate(p, inExpression.isNegated(), cb);
		} else if (exp instanceof True) {
			True<?> trueExpression = (True<?>) exp;
			Expression<?> x = getExpression(trueExpression.getExpression(), cb, rt);
			if (x instanceof Predicate) {
				return getOrNegate(cb.isTrue((Predicate) x), trueExpression.isNegated(), cb);
			}
		} else if (exp instanceof False) {
			False<?> falseExpression = (False<?>) exp;
			Expression<?> x = getExpression(falseExpression.getExpression(), cb, rt);
			if (x instanceof Predicate) {
				return getOrNegate(cb.isFalse((Predicate) x), falseExpression.isNegated(), cb);
			}
		} else if (exp instanceof BinaryExpression) {
			BinaryExpression binaryExpression = (BinaryExpression) exp;
			Expression<?> x = getExpression(binaryExpression.getX(), cb, rt);
			Expression<?> y = getExpression(binaryExpression.getY(), cb, rt);

			switch (binaryExpression.getRelationalOperator()) {
			case EQUAL:
				return getOrNegate(cb.equal(x, y), binaryExpression.isNegated(), cb);
			case GREATER_THAN:
				if (Comparable.class.isAssignableFrom(x.getJavaType())
						&& Comparable.class.isAssignableFrom(y.getJavaType())) {
					return getOrNegate(
							cb.greaterThan((Expression<? extends Comparable>) x, (Expression<? extends Comparable>) y),
							binaryExpression.isNegated(), cb);
				}
				break;
			case GREATER_THAN_OR_EQUAL:
				if (Comparable.class.isAssignableFrom(x.getJavaType())
						&& Comparable.class.isAssignableFrom(y.getJavaType())) {
					return getOrNegate(cb.greaterThanOrEqualTo((Expression<? extends Comparable>) x,
							(Expression<? extends Comparable>) y), binaryExpression.isNegated(), cb);
				}
			case LESS_THAN:
				if (Comparable.class.isAssignableFrom(x.getJavaType())
						&& Comparable.class.isAssignableFrom(y.getJavaType())) {
					return getOrNegate(
							cb.lessThan((Expression<? extends Comparable>) x, (Expression<? extends Comparable>) y),
							binaryExpression.isNegated(), cb);
				}
			case LESS_THAN_OR_EQUAL:
				if (Comparable.class.isAssignableFrom(x.getJavaType())
						&& Comparable.class.isAssignableFrom(y.getJavaType())) {
					return getOrNegate(cb.lessThanOrEqualTo((Expression<? extends Comparable>) x,
							(Expression<? extends Comparable>) y), binaryExpression.isNegated(), cb);
				}
			case NOT_EQUAL:
				return getOrNegate(cb.notEqual(x, y), binaryExpression.isNegated(), cb);
			default:
				break;
			}
		} else if (exp instanceof Between) {
			Between betweenExpression = (Between) exp;
			Expression<?> v = getExpression(betweenExpression.getValue(), cb, rt);
			Expression<?> x = getExpression(betweenExpression.getLowerBound(), cb, rt);
			Expression<?> y = getExpression(betweenExpression.getUpperBound(), cb, rt);
			if (Comparable.class.isAssignableFrom(v.getJavaType())) {
				if (betweenExpression.getLowerBound() instanceof Value
						&& betweenExpression.getUpperBound() instanceof Value) {
					Value x1 = (Value) betweenExpression.getLowerBound();
					Value y1 = (Value) betweenExpression.getUpperBound();
					if (x1.get() instanceof Comparable && y1.get() instanceof Comparable) {
						return getOrNegate(cb.between((Expression<? extends Comparable>) v, (Comparable) x1.get(),
								(Comparable) y1.get()), betweenExpression.isNegated(), cb);
					}
				} else if (Comparable.class.isAssignableFrom(x.getJavaType())
						&& Comparable.class.isAssignableFrom(y.getJavaType())) {
					return getOrNegate(cb.between((Expression<? extends Comparable>) v,
							(Expression<? extends Comparable>) x, (Expression<? extends Comparable>) y),
							betweenExpression.isNegated(), cb);
				}
			}
		} else if (exp instanceof StringExpression) {
			StringExpression stringExpression = (StringExpression) exp;
			Expression<?> x = getExpression(stringExpression.getExpression(), cb, rt);
			switch (stringExpression.getStringOperator()) {
			case CONTAINS:
				return getOrNegate(
						cb.like(cb.upper((Expression<String>) x),
								"%" + stringExpression.getPattern().toUpperCase() + "%"),
						stringExpression.isNegated(), cb);
			case ENDS_WITH:
				return getOrNegate(
						cb.like(cb.upper((Expression<String>) x), "%" + stringExpression.getPattern().toUpperCase()),
						stringExpression.isNegated(), cb);
			case STARTS_WITH:
				return getOrNegate(
						cb.like(cb.upper((Expression<String>) x), stringExpression.getPattern().toUpperCase() + "%"),
						stringExpression.isNegated(), cb);
			default:
				break;

			}
		} else if (exp instanceof org.rainbow.criteria.Predicate) {
			org.rainbow.criteria.Predicate p = (org.rainbow.criteria.Predicate) exp;
			if (p.getExpressions() != null) {
				return getOrNegate(getCompoundExpression(((org.rainbow.criteria.Predicate) exp), cb, rt), p.isNegated(),
						cb);
			}
		}
		throw new IllegalStateException(String.format("No %s could be obtained for the specified %s.",
				Expression.class.getName(), Expression.class.getName()));
	}

	private static <T> Predicate getCompoundExpression(org.rainbow.criteria.Predicate predicate, CriteriaBuilder cb,
			Root<T> rt) {
		Objects.requireNonNull(predicate);
		List<org.rainbow.criteria.Expression<Boolean>> expressions = predicate.getExpressions();
		if (expressions.isEmpty()) {
			throw new IllegalArgumentException("The predicate has no expression.");
		}
		Predicate p = (Predicate) getExpression(expressions.get(0), cb, rt);
		if (predicate.getOperator() == BooleanOperator.OR) {
			for (int i = 1; i < expressions.size(); i++) {
				p = cb.or(p, (Predicate) getExpression(expressions.get(i), cb, rt));
			}
		} else {
			for (int i = 1; i < expressions.size(); i++) {
				p = cb.and(p, (Predicate) getExpression(expressions.get(i), cb, rt));
			}
		}
		return p;
	}

	private static <T> Path<?> getPath(String path, Root<T> rt) {
		String[] parts = path.split(Pattern.quote("."));
		if (parts != null && parts.length > 1) {
			Join<?, ?> p = null;
			for (int i = 0; i < parts.length - 1; i++) {
				if (p == null) {
					p = rt.join(parts[i]);
				} else {
					p = p.join(parts[i]);
				}
			}
			if (p != null) {
				return p.get(parts[parts.length - 1]);
			}
		}
		return rt.get(path);
	}

}
