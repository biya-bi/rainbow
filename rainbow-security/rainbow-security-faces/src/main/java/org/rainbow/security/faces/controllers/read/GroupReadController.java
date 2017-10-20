package org.rainbow.security.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.filters.ListValuedFilter;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.Filterable;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.service.services.GroupService;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class GroupReadController extends AbstractNumericIdAuditableEntityReadController<Group, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2204054302648540191L;

	private static final String NAME_FILTER = "name";
	private static final String APPLICATION_NAME_FILTER = "application.name";
	private static final String USERS_ID_FILTER = "users.id";

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> applicationNameFilter;
	private final ListValuedFilter<String> usersIdFilter;

	@Autowired
	private GroupService groupService;

	public GroupReadController() {
		super(Long.class);
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		applicationNameFilter = new SingleValuedFilter<>(APPLICATION_NAME_FILTER, RelationalOperator.CONTAINS, "");
		usersIdFilter = new ListValuedFilter<>(USERS_ID_FILTER);
		usersIdFilter.setOperator(RelationalOperator.IN);
	}

	@Filterable
	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getApplicationNameFilter() {
		return applicationNameFilter;
	}

	@Filterable
	public ListValuedFilter<String> getUsersIdFilter() {
		return usersIdFilter;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Group> list) {
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Group>() {
					@Override
					public int compare(Group one, Group other) {
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
				Collections.sort(list, new Comparator<Group>() {
					@Override
					public int compare(Group one, Group other) {
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
	public Service<Group> getService() {
		return groupService;
	}

}
