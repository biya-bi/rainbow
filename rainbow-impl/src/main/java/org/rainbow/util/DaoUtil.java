package org.rainbow.util;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.rainbow.criteria.PathImpl;
import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.PredicateBuilderImpl;
import org.rainbow.persistence.dao.Dao;
import org.rainbow.service.UpdateOperation;

public class DaoUtil {

	public static <TEntity extends Object, TKey extends Comparable<? super TKey>, TValue extends Comparable<? super TValue>> boolean isDuplicate(
			Dao<TEntity> dao, String path, TValue value, String idAttributeName, TKey id, UpdateOperation operation)
			throws Exception {
		Objects.requireNonNull(dao);
		Objects.requireNonNull(path);

		return dao.count(constructPredicate(path, value, idAttributeName, id, operation)) > 0;
	}

	public static <TEntity extends Object, TKey extends Comparable<? super TKey>, TValue extends Comparable<? super TValue>> boolean isDuplicate(
			Dao<TEntity> dao, String path, TValue value, TKey id, UpdateOperation operation) throws Exception {
		return isDuplicate(dao, path, value, "id", id, operation);
	}

	public static <TEntity extends Object, TKey extends Comparable<? super TKey>, TValue extends Comparable<? super TValue>, R> boolean isDuplicate(
			Dao<TEntity> dao, Class<R> metadataClass, Function<? super R, String> func, TValue value, TKey id,
			UpdateOperation operation) throws Exception {
		return isDuplicate(dao, func.apply(instantiate(metadataClass)), value, "id", id, operation);
	}

	public static <TEntity extends Object, TKey extends Comparable<? super TKey>> boolean isDuplicate(
			Dao<TEntity> dao, Map<String, Comparable<?>> paths, String idAttributeName, TKey id,
			UpdateOperation operation) throws Exception {
		Objects.requireNonNull(dao);
		Objects.requireNonNull(paths);

		return dao.count(constructPredicate(paths, idAttributeName, id, operation)) > 0;
	}

	public static <TEntity extends Object, TKey extends Comparable<? super TKey>> boolean isDuplicate(
			Dao<TEntity> dao, Map<String, Comparable<?>> pathValuePairs, TKey id, UpdateOperation operation) throws Exception {
		return isDuplicate(dao, pathValuePairs, "id", id, operation);
	}

	private static <TEntity extends Object, TKey extends Comparable<? super TKey>, TValue extends Comparable<? super TValue>> Predicate constructPredicate(
			String path, TValue value, String idAttributeName, TKey id, UpdateOperation operation) {

		switch (operation) {
		case CREATE:
		case UPDATE:
			if (operation == UpdateOperation.UPDATE) {
				if (id == null) {
					throw new IllegalArgumentException(
							String.format("The id argument cannot be null if the operation is '%s'", operation));
				}
			}
			PredicateBuilderImpl builder = new PredicateBuilderImpl();
			Predicate p1 = null;
			if (path != null) {
				p1 = builder.equal(new PathImpl(path), value);
			}
			Predicate p2 = null;
			if (id != null) {
				p2 = builder.notEqual(new PathImpl(idAttributeName), id);
			}
			if (p1 != null && p2 != null) {
				return builder.and(p1, p2);
			} else if (p1 != null) {
				return p1;
			} else if (p2 != null) {
				return p2;
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
		return null;
	}

	private static <TEntity extends Object, TKey extends Comparable<? super TKey>, TValue extends Comparable<? super TValue>> Predicate constructPredicate(
			Map<String, Comparable<?>> pathValuePairs, String idAttributeName, TKey id, UpdateOperation operation) {
		Predicate p = null;

		switch (operation) {
		case CREATE:
		case UPDATE:
			PredicateBuilderImpl builder = new PredicateBuilderImpl();

			for (String path : pathValuePairs.keySet()) {
				Predicate p1 = builder.equal(new PathImpl(path), pathValuePairs.get(path));
				if (p == null) {
					p = p1;
				} else {
					p = builder.and(p, p1);
				}
			}
			if (operation == UpdateOperation.UPDATE) {
				if (id == null) {
					throw new IllegalArgumentException(
							String.format("The id argument cannot be null if the operation is '%s'", operation));
				}
				p = builder.and(p, builder.notEqual(new PathImpl(idAttributeName), id));
			}
			break;
		case DELETE:
			break;
		default:
			break;
		}
		return p;
	}

	private static <T> T instantiate(Class<T> cls) throws InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Objects.requireNonNull(cls);
		return cls.getConstructor().newInstance(new Object[] {});
	}
}
