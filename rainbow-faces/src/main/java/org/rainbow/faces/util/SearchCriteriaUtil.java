package org.rainbow.faces.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.rainbow.criteria.Path;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.search.criteria.AbstractSearchCriterion;
import org.rainbow.search.criteria.IntervalSearchCriterion;
import org.rainbow.search.criteria.ListSearchCriterion;
import org.rainbow.search.criteria.ComparableSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;

public class SearchCriteriaUtil {

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
			SearchCriterion[] annotations = m.getAnnotationsByType(SearchCriterion.class);
			if (annotations.length > 0) {
				Object value;
				try {
					value = m.invoke(obj, new Object[0]);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException(e);
				}
				if (value instanceof AbstractSearchCriterion) {
					Predicate predicate = getPredicate((AbstractSearchCriterion) value, predicateBuilderFactory,
							pathFactory);
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

	public static Predicate getPredicate(AbstractSearchCriterion searchCriterion,
			PredicateBuilderFactory predicateBuilderFactory, PathFactory pathFactory) {

		Objects.requireNonNull(searchCriterion);
		Objects.requireNonNull(predicateBuilderFactory);
		Objects.requireNonNull(pathFactory);

		if (searchCriterion instanceof StringSearchCriterion) {
			return getPredicate((StringSearchCriterion) searchCriterion, predicateBuilderFactory, pathFactory);
		}
		if (searchCriterion instanceof ComparableSearchCriterion<?>) {
			return getPredicate((ComparableSearchCriterion<?>) searchCriterion, predicateBuilderFactory, pathFactory);
		}
		if (searchCriterion instanceof ListSearchCriterion<?>) {
			return getPredicate((ListSearchCriterion<?>) searchCriterion, predicateBuilderFactory, pathFactory);
		}
		if (searchCriterion instanceof IntervalSearchCriterion<?>) {
			return getPredicate((IntervalSearchCriterion<?>) searchCriterion, predicateBuilderFactory, pathFactory);
		}
		return null;
	}

	private static Predicate getPredicate(StringSearchCriterion searchCriterion,
			PredicateBuilderFactory predicateBuilderFactory, PathFactory pathFactory) {

		if (searchCriterion.getOperator() != null) {
			PredicateBuilder builder = predicateBuilderFactory.create();
			Path path = pathFactory.create(searchCriterion.getPath());
			String value = searchCriterion.getValue();

			boolean hasValue = value != null && !value.isEmpty();

			switch (searchCriterion.getOperator()) {
			case CONTAINS:
				if (hasValue) {
					return builder.contains(path, value);
				}
				break;
			case DOES_NOT_CONTAIN:
				if (hasValue) {
					return builder.not(builder.contains(path, value));
				}
				break;
			case ENDS_WITH:
				if (hasValue) {
					return builder.endsWith(path, value);
				}
				break;
			case EQUAL:
				if (hasValue) {
					return builder.equal(path, value);
				}
				break;
			case GREATER_THAN:
				if (hasValue) {
					return builder.greaterThan(path, value);
				}
				break;
			case GREATER_THAN_OR_EQUAL:
				if (hasValue) {
					return builder.greaterThanOrEqualTo(path, value);
				}
				break;
			case IS_EMPTY:
				return builder.or(builder.isNull(path), builder.equal(path, ""));
			case IS_NOT_EMPTY:
				return builder.not(builder.or(builder.isNull(path), builder.equal(path, "")));
			case LESS_THAN:
				if (hasValue) {
					return builder.lessThan(path, value);
				}
				break;
			case LESS_THAN_OR_EQUAL:
				if (hasValue) {
					return builder.lessThanOrEqualTo(path, value);
				}
				break;
			case NOT_EQUAL:
				if (hasValue) {
					return builder.notEqual(path, value);
				}
				break;
			case STARTS_WITH:
				if (hasValue) {
					return builder.startsWith(path, value);
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Predicate getPredicate(ComparableSearchCriterion<?> searchCriterion,
			PredicateBuilderFactory predicateBuilderFactory, PathFactory pathFactory) {

		if (searchCriterion.getOperator() != null) {
			PredicateBuilder builder = predicateBuilderFactory.create();
			Path path = pathFactory.create(searchCriterion.getPath());
			Comparable<?> value = searchCriterion.getValue();

			boolean hasValue = value != null;

			switch (searchCriterion.getOperator()) {
			case EQUAL:
				if (hasValue) {
					return builder.equal(path, value);
				}
				break;
			case GREATER_THAN:
				if (hasValue) {
					return builder.greaterThan(path, (Comparable) value);
				}
				break;
			case GREATER_THAN_OR_EQUAL:
				if (hasValue) {
					return builder.greaterThanOrEqualTo(path, (Comparable) value);
				}
				break;
			case IS_EMPTY:
				return builder.isNull(path);
			case IS_NOT_EMPTY:
				return builder.isNotNull(path);
			case LESS_THAN:
				if (hasValue) {
					return builder.lessThan(path, (Comparable) value);
				}
				break;
			case LESS_THAN_OR_EQUAL:
				if (hasValue) {
					return builder.lessThanOrEqualTo(path, (Comparable) value);
				}
				break;
			case NOT_EQUAL:
				if (hasValue) {
					return builder.notEqual(path, value);
				}
				break;
			default:
				break;
			}
		}
		return null;
	}

	private static Predicate getPredicate(ListSearchCriterion<?> searchCriterion,
			PredicateBuilderFactory predicateBuilderFactory, PathFactory pathFactory) {

		if (searchCriterion.getOperator() != null) {
			PredicateBuilder builder = predicateBuilderFactory.create();
			Path path = pathFactory.create(searchCriterion.getPath());
			List<?> list = searchCriterion.getList();

			if (list != null) {
				switch (searchCriterion.getOperator()) {
				case IN:
					return builder.in(path, list);
				case NOT_IN:
					return builder.not(builder.in(path, list));
				default:
					break;
				}
			}
		}
		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Predicate getPredicate(IntervalSearchCriterion<?> searchCriterion,
			PredicateBuilderFactory predicateBuilderFactory, PathFactory pathFactory) {

		if (searchCriterion.getOperator() != null) {
			PredicateBuilder builder = predicateBuilderFactory.create();
			Path path = pathFactory.create(searchCriterion.getPath());

			Comparable lowerBound = searchCriterion.getLowerBound();
			Comparable upperBound = searchCriterion.getUpperBound();

			boolean hasValue = lowerBound != null && upperBound != null;
			if (hasValue) {
				switch (searchCriterion.getOperator()) {
				case LEFT_CLOSED_RIGHT_CLOSED:
					return builder.and(builder.greaterThanOrEqualTo(path, lowerBound),
							builder.lessThanOrEqualTo(path, upperBound));
				case LEFT_CLOSED_RIGHT_CLOSED_COMPLIMENT:
					return builder.not(builder.and(builder.greaterThanOrEqualTo(path, lowerBound),
							builder.lessThanOrEqualTo(path, upperBound)));
				case LEFT_CLOSED_RIGHT_OPEN:
					return builder.and(builder.greaterThanOrEqualTo(path, lowerBound),
							builder.lessThan(path, upperBound));
				case LEFT_CLOSED_RIGHT_OPEN_COMPLIMENT:
					return builder.not(builder.and(builder.greaterThanOrEqualTo(path, lowerBound),
							builder.lessThan(path, upperBound)));
				case LEFT_OPEN_RIGHT_CLOSED:
					return builder.and(builder.greaterThan(path, lowerBound),
							builder.lessThanOrEqualTo(path, upperBound));
				case LEFT_OPEN_RIGHT_CLOSED_COMPLIMENT:
					return builder.not(builder.and(builder.greaterThan(path, lowerBound),
							builder.lessThanOrEqualTo(path, upperBound)));
				case LEFT_OPEN_RIGHT_OPEN:
					return builder.and(builder.greaterThan(path, lowerBound), builder.lessThan(path, upperBound));
				case LEFT_OPEN_RIGHT_OPEN_COMPLIMENT:
					return builder.not(
							builder.and(builder.greaterThan(path, lowerBound), builder.lessThan(path, upperBound)));
				default:
					break;
				}
			}
		}
		return null;
	}

}
