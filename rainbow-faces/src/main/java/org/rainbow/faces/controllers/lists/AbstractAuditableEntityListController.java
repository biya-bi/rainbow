package org.rainbow.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.Filterable;
import org.rainbow.orm.entities.AbstractAuditableEntity;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 */
public abstract class AbstractAuditableEntityListController<TModel extends AbstractAuditableEntity<?>> extends AbstractListController<TModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2818461029343555835L;
	private static final String CREATOR_FILTER = "creator";
	private static final String UPDATER_FILTER = "updater";
	private static final String CREATION_DATE_FILTER = "creationDate";
	private static final String LAST_UPDATE_DATE_FILTER = "lastUpdateDate";

	private final SingleValuedFilter<String> creatorFilter;
	private final SingleValuedFilter<String> updaterFilter;
	private final SingleValuedFilter<Date> creationDateFilter;
	private final SingleValuedFilter<Date> lastUpdateDateFilter;

	public AbstractAuditableEntityListController() {
		creatorFilter = new SingleValuedFilter<>(CREATOR_FILTER, RelationalOperator.CONTAINS, "");
		updaterFilter = new SingleValuedFilter<>(UPDATER_FILTER, RelationalOperator.CONTAINS, "");
		creationDateFilter = new SingleValuedFilter<>(CREATION_DATE_FILTER, RelationalOperator.EQUAL, null);
		lastUpdateDateFilter = new SingleValuedFilter<>(LAST_UPDATE_DATE_FILTER, RelationalOperator.EQUAL, null);
	}

	@Filterable
	public SingleValuedFilter<String> getCreatorFilter() {
		return creatorFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getUpdaterFilter() {
		return updaterFilter;
	}

	@Filterable
	public SingleValuedFilter<Date> getCreationDateFilter() {
		return creationDateFilter;
	}

	@Filterable
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
