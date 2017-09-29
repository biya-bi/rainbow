package org.rainbow.security.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.persistence.Filter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component @Named
@ViewScoped
public class AuthorityLazyDataModel extends LongIdTrackableLazyDataModel<Authority> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3920384029343681244L;

	private static final String NAME_FILTER = "name";
	private static final String APPLICATION_NAME_FILTER = "application.name";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> applicationNameFilter;

	@Autowired
	@Qualifier("authorityService")
	private Service<Authority, Long, SearchOptions> authorityService;

	public AuthorityLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		applicationNameFilter = new SingleValuedFilter<>(APPLICATION_NAME_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(applicationNameFilter);
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getApplicationNameFilter() {
		return applicationNameFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		return filters;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Authority> list) {
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Authority>() {
					@Override
					public int compare(Authority one, Authority other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case APPLICATION_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Authority>() {
					@Override
					public int compare(Authority one, Authority other) {
						int result = comparator.compare(one.getApplication().getName(),
								other.getApplication().getName());
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
	protected Service<Authority, Long, SearchOptions> getService() {
		return authorityService;
	}

}
