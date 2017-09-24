/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.ui.web.lazy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.journal.core.entities.Publication;
import org.rainbow.journal.ui.web.utilities.DefaultComparator;
import org.rainbow.service.Service;
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
public class PublicationLazyDataModel extends LongIdTrackableLazyDataModel<Publication> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822591215007082723L;

	@Autowired
	@Qualifier("publicationService")
	private Service<Publication, Long, SearchOptions> service;

	private static final String PUBLICATION_DATE_FILTER = "publicationDate";
	private static final String JOURNAL_NAME_FILTER = "journal.name";
	private static final String PUBLISHER_USER_NAME_FILTER = "publisherProfile.userName";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<Date> publicationDateFilter;
	private final SingleValuedFilter<String> journalNameFilter;
	private final SingleValuedFilter<String> publisherUserNameFilter;

	public PublicationLazyDataModel() {
		publicationDateFilter = new SingleValuedFilter<>(PUBLICATION_DATE_FILTER, RelationalOperator.CONTAINS, null);
		journalNameFilter = new SingleValuedFilter<>(JOURNAL_NAME_FILTER, RelationalOperator.CONTAINS, "");
		publisherUserNameFilter = new SingleValuedFilter<>(PUBLISHER_USER_NAME_FILTER,
				RelationalOperator.CONTAINS, null);

		filters = new ArrayList<>();
		filters.add(publicationDateFilter);
		filters.add(journalNameFilter);
		filters.add(publisherUserNameFilter);
	}

	@Override
	protected Service<Publication, Long, SearchOptions> getService() {
		return service;
	}

	public SingleValuedFilter<String> getJournalNameFilter() {
		return journalNameFilter;
	}

	public SingleValuedFilter<Date> getPublicationDateFilter() {
		return publicationDateFilter;
	}

	public SingleValuedFilter<String> getPublisherUserNameFilter() {
		return publisherUserNameFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		// A publication will always have a publication date. Its publication
		// date
		// cannot be null. Therefore, if the value of the publication date
		// filter is
		// a date, we add (if not already included) it to the filters, else
		// we remove (if already included) it from the filters
		if (publicationDateFilter.getValue() instanceof Date) {
			if (!filters.contains(publicationDateFilter))
				filters.add(publicationDateFilter);
		} else {
			if (filters.contains(publicationDateFilter))
				filters.remove(publicationDateFilter);
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
	protected void sort(String sortField, SortOrder sortOrder, List<Publication> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = JOURNAL_NAME_FILTER;
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case PUBLICATION_DATE_FILTER: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<Publication>() {
					@Override
					public int compare(Publication one, Publication other) {
						int result = comparator.compare(one.getPublicationDate(), other.getPublicationDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case JOURNAL_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Publication>() {
					@Override
					public int compare(Publication one, Publication other) {
						if (one.getJournal() == null && other.getJournal() == null) {
							return 0;
						}
						if (one.getJournal() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getJournal() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getJournal().getName(), other.getJournal().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case PUBLISHER_USER_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Publication>() {
					@Override
					public int compare(Publication one, Publication other) {
						int result = comparator.compare(one.getPublisherProfile().getUserName(),
								other.getPublisherProfile().getUserName());
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
