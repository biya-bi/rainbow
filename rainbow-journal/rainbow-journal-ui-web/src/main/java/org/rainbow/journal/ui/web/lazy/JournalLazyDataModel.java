/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.ui.web.lazy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.service.Service;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.ui.web.utilities.DefaultComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@RequestScoped
public class JournalLazyDataModel extends LongIdTrackableLazyDataModel<Journal> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6796618236115317151L;

	@Autowired
	@Qualifier("journalService")
	private Service<Journal, Long, SearchOptions> service;

	private static final String NAME_FILTER = "name";
	private static final String OWNER_PROFILE_USER_NAME_FILTER = "ownerProfile.userName";
	private static final String ACTIVE_FILTER = "active";
	private static final String TAG_FILTER = "tag";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> ownerProfileUserNameFilter;
	private final SingleValuedFilter<Boolean> activeFilter;
	private final SingleValuedFilter<String> tagFilter;

	public JournalLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		ownerProfileUserNameFilter = new SingleValuedFilter<>(OWNER_PROFILE_USER_NAME_FILTER,
				RelationalOperator.CONTAINS, "");
		activeFilter = new SingleValuedFilter<>(ACTIVE_FILTER, RelationalOperator.EQUAL, null);
		tagFilter = new SingleValuedFilter<>(TAG_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(ownerProfileUserNameFilter);
		filters.add(activeFilter);
		filters.add(tagFilter);
	}

	@Override
	protected Service<Journal, Long, SearchOptions> getService() {
		return service;
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getOwnerProfileUserNameFilter() {
		return ownerProfileUserNameFilter;
	}

	public SingleValuedFilter<Boolean> getActiveFilter() {
		return activeFilter;
	}

	public SingleValuedFilter<String> getTagFilter() {
		return tagFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		// A journal will always be either active or not active. Its active
		// state cannot be null. Therefore, if the value of the active filter is
		// a boolean, we add (if not already included) it to the filters, else
		// we remove (if already included) it from the filters
		if (activeFilter.getValue() instanceof Boolean) {
			if (!filters.contains(activeFilter))
				filters.add(activeFilter);
		} else {
			if (filters.contains(activeFilter))
				filters.remove(activeFilter);
		}
		
		List<Filter<?>> baseFilters = super.getFilters();
		
		if (baseFilters != null) {
			ArrayList<Filter<?>> combinedFilters = new ArrayList<>(baseFilters);
			combinedFilters.addAll(filters);
			return combinedFilters;
		}
		return filters;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Journal> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case OWNER_PROFILE_USER_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.getOwnerProfile().getUserName(),
								other.getOwnerProfile().getUserName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case ACTIVE_FILTER: {
				final Comparator<Boolean> comparator = DefaultComparator.<Boolean>getInstance();
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.isActive(), other.isActive());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case TAG_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.getTag(), other.getTag());
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
