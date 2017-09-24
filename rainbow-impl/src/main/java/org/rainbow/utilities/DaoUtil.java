package org.rainbow.utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.rainbow.persistence.Dao;
import org.rainbow.persistence.Filter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.service.UpdateOperation;

public class DaoUtil {

	public static <TEntity extends Object, TKey extends Serializable & Comparable<? super TKey>, TValue extends Comparable<? super TValue>> boolean isDuplicate(
			Dao<TEntity, TKey, SearchOptions> dao, String filterName, TValue filterValue, String idAttributeName,
			TKey id, UpdateOperation operation) throws Exception {
		if (dao == null) {
			throw new IllegalArgumentException("The dao argument cannot be null.");
		}
		if (filterName == null) {
			throw new IllegalArgumentException("The filterName argument cannot be null.");
		}
		return dao.count(getSearchOptions(filterName, filterValue, idAttributeName, id, operation)) > 0;
	}

	public static <TEntity extends Object, TKey extends Serializable & Comparable<? super TKey>, TValue extends Comparable<? super TValue>> boolean isDuplicate(
			Dao<TEntity, TKey, SearchOptions> dao, String filterName, TValue value, TKey id, UpdateOperation operation)
			throws Exception {
		return isDuplicate(dao, filterName, value, "id", id, operation);
	}

	public static <TEntity extends Object, TKey extends Serializable & Comparable<? super TKey>> boolean isDuplicate(
			Dao<TEntity, TKey, SearchOptions> dao, Map<String, Comparable<?>> filters, String idAttributeName, TKey id,
			UpdateOperation operation) throws Exception {
		if (dao == null) {
			throw new IllegalArgumentException("The dao argument cannot be null.");
		}
		if (filters == null) {
			throw new IllegalArgumentException("The filters argument cannot be null.");
		}
		return dao.count(getSearchOptions(filters, idAttributeName, id, operation)) > 0;
	}

	public static <TEntity extends Object, TKey extends Serializable & Comparable<? super TKey>> boolean isDuplicate(
			Dao<TEntity, TKey, SearchOptions> dao, Map<String, Comparable<?>> filters, TKey id,
			UpdateOperation operation) throws Exception {
		return isDuplicate(dao, filters, "id", id, operation);
	}

	private static <TEntity extends Object, TKey extends Serializable & Comparable<? super TKey>, TValue extends Comparable<? super TValue>> SearchOptions getSearchOptions(
			String filterName, TValue filterValue, String idAttributeName, TKey id, UpdateOperation operation) {
		final SearchOptions options = new SearchOptions();

		switch (operation) {
		case CREATE:
		case UPDATE:
			final List<Filter<?>> filters = new ArrayList<>();

			final SingleValuedFilter<TValue> valueEqualFilter = new SingleValuedFilter<TValue>(filterName,
					RelationalOperator.EQUAL, filterValue);

			filters.add(valueEqualFilter);
			if (operation == UpdateOperation.UPDATE) {
				if (id == null) {
					throw new IllegalArgumentException(
							String.format("The id argument cannot be null if the operation is '%s'", operation));
				}
				final SingleValuedFilter<TKey> idNotEqualFilter = new SingleValuedFilter<TKey>(idAttributeName,
						RelationalOperator.NOT_EQUAL, id);
				filters.add(idNotEqualFilter);
			}
			options.setFilters(filters);
			break;
		case DELETE:
			break;
		default:
			break;
		}
		return options;
	}

	private static <TEntity extends Object, TKey extends Serializable & Comparable<? super TKey>, TValue extends Comparable<? super TValue>> SearchOptions getSearchOptions(
			Map<String, Comparable<?>> filters, String idAttributeName, TKey id, UpdateOperation operation) {
		final SearchOptions options = new SearchOptions();

		switch (operation) {
		case CREATE:
		case UPDATE:
			final List<Filter<?>> flts = new ArrayList<>();

			filters.keySet().stream().forEach(filterName -> {
				@SuppressWarnings("rawtypes")
				SingleValuedFilter<?> filter = new SingleValuedFilter<>(filterName, RelationalOperator.EQUAL,
						(Comparable) filters.get(filterName));
				flts.add(filter);
			});
			if (operation == UpdateOperation.UPDATE) {
				if (id == null) {
					throw new IllegalArgumentException(
							String.format("The id argument cannot be null if the operation is '%s'", operation));
				}
				SingleValuedFilter<TKey> idNotEqualFilter = new SingleValuedFilter<TKey>(idAttributeName,
						RelationalOperator.NOT_EQUAL, id);
				flts.add(idNotEqualFilter);
			}
			options.setFilters(flts);
			break;
		case DELETE:
			break;
		default:
			break;
		}
		return options;
	}

}
