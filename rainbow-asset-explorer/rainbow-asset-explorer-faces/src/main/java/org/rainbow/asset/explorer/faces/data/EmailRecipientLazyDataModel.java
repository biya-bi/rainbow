/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.core.entities.EmailRecipient;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class EmailRecipientLazyDataModel extends IntegerIdTrackableLazyDataModel<EmailRecipient> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1700034370905504497L;

	private static final String NAME_FILTER = "name";
	private static final String EMAIL_FILTER = "email";
	private static final String LOCALE_NAME_FILTER = "locale.name";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;

	private final SingleValuedFilter<String> emailFilter;
	private final SingleValuedFilter<String> localeNameFilter;

	@Autowired
	@Qualifier("emailRecipientService")
	private Service<EmailRecipient, Integer, SearchOptions> service;

	public EmailRecipientLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		emailFilter = new SingleValuedFilter<>(EMAIL_FILTER, RelationalOperator.CONTAINS, "");
		localeNameFilter = new SingleValuedFilter<>(LOCALE_NAME_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(emailFilter);
		filters.add(localeNameFilter);
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getEmailFilter() {
		return emailFilter;
	}

	public SingleValuedFilter<String> getLocaleNameFilter() {
		return localeNameFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		List<Filter<?>> baseFilters = super.getFilters();
		if (baseFilters != null) {
			ArrayList<Filter<?>> combinedFilters = new ArrayList<>(baseFilters);
			combinedFilters.addAll(filters);
			return combinedFilters;
		}
		return filters;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<EmailRecipient> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = EMAIL_FILTER; // We want to sort by name if no sort
										// field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<EmailRecipient>() {
					@Override
					public int compare(EmailRecipient one, EmailRecipient other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case EMAIL_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<EmailRecipient>() {
					@Override
					public int compare(EmailRecipient one, EmailRecipient other) {
						int result = comparator.compare(one.getEmail(), other.getEmail());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LOCALE_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<EmailRecipient>() {
					@Override
					public int compare(EmailRecipient one, EmailRecipient other) {
						int result = comparator.compare(one.getLocale().getName(), other.getLocale().getName());
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

	@Override
	protected Service<EmailRecipient, Integer, SearchOptions> getService() {
		return service;
	}

}
