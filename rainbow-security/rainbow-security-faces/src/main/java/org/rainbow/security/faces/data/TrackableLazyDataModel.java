package org.rainbow.security.faces.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.orm.entities.Trackable;
import org.rainbow.persistence.Filter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SingleValuedFilter;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 * @param <TId>
 */
public abstract class TrackableLazyDataModel<TModel extends Trackable<?>, TId extends Serializable>
		extends AbstractLazyDataModel<TModel, TId> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9130830969689107523L;
	
	private static final String CREATOR_FILTER = "creator";
	private static final String UPDATER_FILTER = "updater";
	private static final String CREATION_DATE_FILTER = "creationDate";
	private static final String LAST_UPDATE_DATE_FILTER = "lastUpdateDate";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> creatorFilter;
	private final SingleValuedFilter<String> updaterFilter;
	private final SingleValuedFilter<Date> creationDateFilter;
	private final SingleValuedFilter<Date> lastUpdateDateFilter;

	public TrackableLazyDataModel() {

		creatorFilter = new SingleValuedFilter<>(CREATOR_FILTER, RelationalOperator.CONTAINS, "");
		updaterFilter = new SingleValuedFilter<>(UPDATER_FILTER, RelationalOperator.CONTAINS, "");
		creationDateFilter = new SingleValuedFilter<>(CREATION_DATE_FILTER, RelationalOperator.EQUAL, null);
		lastUpdateDateFilter = new SingleValuedFilter<>(LAST_UPDATE_DATE_FILTER, RelationalOperator.EQUAL, null);

		filters = new ArrayList<>();
		filters.add(creatorFilter);
		filters.add(updaterFilter);
		filters.add(creationDateFilter);
		filters.add(lastUpdateDateFilter);
	}

	@Override
	protected List<Filter<?>> getFilters() {
		filters.remove(creationDateFilter);
		if (creationDateFilter.getValue() != null)
			filters.add(creationDateFilter);
		filters.remove(lastUpdateDateFilter);
		if (lastUpdateDateFilter.getValue() != null)
			filters.add(lastUpdateDateFilter);
		return filters;
	}

	public SingleValuedFilter<String> getCreatorFilter() {
		return creatorFilter;
	}

	public SingleValuedFilter<String> getUpdaterFilter() {
		return updaterFilter;
	}

	public SingleValuedFilter<Date> getCreationDateFilter() {
		return creationDateFilter;
	}

	public SingleValuedFilter<Date> getLastUpdateDateFilter() {
		return lastUpdateDateFilter;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<TModel> list) {
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case CREATOR_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<TModel>() {
					@Override
					public int compare(TModel one, TModel other) {
						int result = comparator.compare(one.getCreator(), other.getCreator());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case UPDATER_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<TModel>() {
					@Override
					public int compare(TModel one, TModel other) {
						int result = comparator.compare(one.getUpdater(), other.getUpdater());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}

			case CREATION_DATE_FILTER: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<TModel>() {
					@Override
					public int compare(TModel one, TModel other) {
						int result = comparator.compare(one.getCreationDate(), other.getCreationDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LAST_UPDATE_DATE_FILTER: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<TModel>() {
					@Override
					public int compare(TModel one, TModel other) {
						int result = comparator.compare(one.getLastUpdateDate(), other.getLastUpdateDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			default:
				break;
			}
		}
	}
}
