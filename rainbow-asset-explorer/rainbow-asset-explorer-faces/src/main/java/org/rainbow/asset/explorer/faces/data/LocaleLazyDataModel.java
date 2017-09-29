package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.persistence.Filter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.service.services.Service;
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
public class LocaleLazyDataModel extends IntegerIdTrackableLazyDataModel<Locale> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 59389858090861992L;

	private static final String NAME_FILTER = "name";
	private static final String LANGUAGE_CODE_FILTER = "languageCode";
	private static final String LCID_FILTER = "lcid";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> languageCodeFilter;
	private final SingleValuedFilter<String> lcidFilter;

	@Autowired
	@Qualifier("localeService")
	private Service<Locale, Integer, SearchOptions> service;

	public LocaleLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		languageCodeFilter = new SingleValuedFilter<>(LANGUAGE_CODE_FILTER, RelationalOperator.CONTAINS, "");
		lcidFilter = new SingleValuedFilter<>(LCID_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(languageCodeFilter);
		filters.add(lcidFilter);
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getLanguageCodeFilter() {
		return languageCodeFilter;
	}

	public SingleValuedFilter<String> getLcidFilter() {
		return lcidFilter;
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
	protected void sort(String sortField, SortOrder sortOrder, List<Locale> list) {
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
				Collections.sort(list, new Comparator<Locale>() {
					@Override
					public int compare(Locale one, Locale other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LANGUAGE_CODE_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Locale>() {
					@Override
					public int compare(Locale one, Locale other) {
						int result = comparator.compare(one.getLanguageCode(), other.getLanguageCode());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LCID_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Locale>() {
					@Override
					public int compare(Locale one, Locale other) {
						int result = comparator.compare(one.getLcid(), other.getLcid());
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
	protected Service<Locale, Integer, SearchOptions> getService() {
		return service;
	}

}
