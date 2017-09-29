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
import org.rainbow.security.orm.entities.Application;
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
public class ApplicationLazyDataModel extends LongIdTrackableLazyDataModel<Application> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5957873059508715867L;

	private static final String NAME_FILTER = "name";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;

	@Autowired
	@Qualifier("applicationService")
	private Service<Application, Long, SearchOptions> applicationService;

	public ApplicationLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		return filters;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Application> list) {
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Application>() {
					@Override
					public int compare(Application one, Application other) {
						int result = comparator.compare(one.getName(), other.getName());
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
	protected Service<Application, Long, SearchOptions> getService() {
		return applicationService;
	}

}
