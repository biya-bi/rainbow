package org.rainbow.security.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.lists.AbstractNumericIdAuditableListController;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.Filterable;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.service.services.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class ApplicationListController extends AbstractNumericIdAuditableListController<Application, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5957873059508715867L;

	private static final String NAME_FILTER = "name";

	private final SingleValuedFilter<String> nameFilter;

	@Autowired
	private ApplicationService applicationService;

	public ApplicationListController() {
		super(Long.class);
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, null);
	}

	@Filterable
	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
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
	protected ApplicationService getService() {
		return applicationService;
	}

}
