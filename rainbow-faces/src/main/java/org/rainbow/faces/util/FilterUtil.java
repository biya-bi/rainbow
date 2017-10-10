package org.rainbow.faces.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.rainbow.criteria.Path;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.faces.filters.Filter;
import org.rainbow.faces.filters.ListValuedFilter;
import org.rainbow.faces.filters.SingleValuedFilter;

public class FilterUtil {

	public static Predicate getPredicate(Object obj, PredicateBuilderFactory predicateBuilderFactory,
			PathFactory pathFactory) {

		Objects.requireNonNull(obj);
		Objects.requireNonNull(predicateBuilderFactory);
		Objects.requireNonNull(pathFactory);

		List<Predicate> predicates = new ArrayList<>();

		Class<? extends Object> cls = obj.getClass();
		Method[] methods = cls.getMethods();
		for (Method m : methods) {
			if (m.getParameterCount() != 0) {
				continue;
			}
			Filterable[] annotations = m.getAnnotationsByType(Filterable.class);
			if (annotations.length > 0) {
				Object value;
				try {
					value = m.invoke(obj, new Object[0]);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				if (value instanceof Filter) {
					Predicate predicate = getPredicate((Filter<?>) value, predicateBuilderFactory, pathFactory);
					if (predicate != null) {
						predicates.add(predicate);
					}
				}
			}
		}
		if (!predicates.isEmpty()) {
			return predicateBuilderFactory.create().and(predicates.toArray(new Predicate[predicates.size()]));
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Predicate getPredicate(Filter<?> filter, PredicateBuilderFactory predicateBuilderFactory,
			PathFactory pathFactory) {

		Objects.requireNonNull(filter);
		Objects.requireNonNull(predicateBuilderFactory);
		Objects.requireNonNull(pathFactory);

		if (filter.getOperator() != null) {
			PredicateBuilder builder = predicateBuilderFactory.create();

			Path p = pathFactory.create(filter.getPath());
			Object value = null;
			if (filter instanceof SingleValuedFilter) {
				value = ((SingleValuedFilter) filter).getValue();
			} else if (filter instanceof ListValuedFilter) {
				value = ((ListValuedFilter) filter).getList();
			}
			boolean hasValue = value != null;
			boolean isString = false;
			if (value instanceof String) {
				isString = true;
				hasValue = !((String) value).trim().isEmpty();
			}
			switch (filter.getOperator()) {
			case CONTAINS:
				if (isString && hasValue) {
					return builder.contains(p, (String) value);
				}
				break;
			case DOES_NOT_CONTAIN:
				if (isString && hasValue) {
					return builder.not(builder.contains(p, (String) value));
				}
				break;
			case ENDS_WITH:
				if (isString && hasValue) {
					return builder.endsWith(p, (String) value);
				}
				break;
			case EQUAL:
				if (hasValue) {
					return builder.equal(p, value);
				}
				break;
			case GREATER_THAN:
				if (hasValue) {
					return builder.greaterThan(p, (Comparable) value);
				}
				break;
			case GREATER_THAN_OR_EQUAL:
				if (hasValue) {
					return builder.greaterThanOrEqualTo(p, (Comparable) value);
				}
				break;
			case IN:
				if (hasValue) {
					return builder.in(p, (Collection<?>) value);
				}
				break;
			case IS_BETWEEN:
				break;
			case IS_EMPTY:
				if (isString && hasValue) {
					return builder.or(builder.isNull(p), builder.equal(p, ""));
				}
				return builder.isNull(p);
			case IS_NOT_BETWEEN:
				break;
			case IS_NOT_EMPTY:
				if (isString && hasValue) {
					return builder.not(builder.or(builder.isNull(p), builder.equal(p, "")));
				}
				return builder.isNotNull(p);
			case LESS_THAN:
				if (hasValue) {
					return builder.lessThan(p, (Comparable) value);
				}
				break;
			case LESS_THAN_OR_EQUAL:
				if (hasValue) {
					return builder.lessThanOrEqualTo(p, (Comparable) value);
				}
				break;
			case NOT_EQUAL:
				if (hasValue) {
					return builder.notEqual(p, value);
				}
				break;
			case NOT_IN:
				if (hasValue) {
					return builder.not(builder.in(p, (Collection<?>) value));
				}
				break;
			case STARTS_WITH:
				if (isString && hasValue) {
					return builder.startsWith(p, (String) value);
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

}
