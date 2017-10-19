package org.rainbow.security.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.lists.AbstractNumericIdAuditableEntityListController;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.Filterable;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class UserListController extends AbstractNumericIdAuditableEntityListController<User, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3766648975654337021L;

	private static final String USER_NAME_FILTER = "userName";
	private static final String APPLICATION_NAME_FILTER = "application.name";
	private static final String ENABLED_FILTER = "membership.enabled";
	private static final String LOCKED_OUT_FILTER = "membership.locked";
	private static final String EMAIL_FILTER = "membership.email";
	private static final String PHONE_FILTER = "membership.phone";

	private final SingleValuedFilter<String> userNameFilter;
	private final SingleValuedFilter<String> applicationNameFilter;
	private final SingleValuedFilter<Boolean> enabledFilter;
	private final SingleValuedFilter<Boolean> lockedFilter;
	private final SingleValuedFilter<String> emailFilter;
	private final SingleValuedFilter<String> phoneFilter;

	@Autowired
	private UserService userService;

	public UserListController() {
		super(Long.class);
		userNameFilter = new SingleValuedFilter<>(USER_NAME_FILTER, RelationalOperator.CONTAINS, "");
		applicationNameFilter = new SingleValuedFilter<>(APPLICATION_NAME_FILTER, RelationalOperator.CONTAINS, "");
		enabledFilter = new SingleValuedFilter<>(ENABLED_FILTER, RelationalOperator.EQUAL, null);
		lockedFilter = new SingleValuedFilter<>(LOCKED_OUT_FILTER, RelationalOperator.EQUAL, null);
		emailFilter = new SingleValuedFilter<>(EMAIL_FILTER);
		phoneFilter = new SingleValuedFilter<>(PHONE_FILTER);
	}

	@Filterable
	public SingleValuedFilter<String> getUserNameFilter() {
		return userNameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getApplicationNameFilter() {
		return applicationNameFilter;
	}

	@Filterable
	public SingleValuedFilter<Boolean> getEnabledFilter() {
		return enabledFilter;
	}

	@Filterable
	public SingleValuedFilter<Boolean> getLockedFilter() {
		return lockedFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getEmailFilter() {
		return emailFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getPhoneFilter() {
		return phoneFilter;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<User> list) {
		if (sortField == null) {
			sortField = USER_NAME_FILTER; // We want to sort by user name if no
											// sort field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case USER_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<User>() {
					@Override
					public int compare(User one, User other) {
						int result = comparator.compare(one.getUserName(), other.getUserName());
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
				Collections.sort(list, new Comparator<User>() {
					@Override
					public int compare(User one, User other) {
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
			case ENABLED_FILTER: {
				final Comparator<Boolean> comparator = DefaultComparator.<Boolean>getInstance();
				Collections.sort(list, new Comparator<User>() {
					@Override
					public int compare(User one, User other) {
						if (one.getMembership() == null && other.getMembership() == null) {
							return 0;
						}
						if (one.getMembership() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getMembership() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getMembership().isEnabled(),
								other.getMembership().isEnabled());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LOCKED_OUT_FILTER: {
				final Comparator<Boolean> comparator = DefaultComparator.<Boolean>getInstance();
				Collections.sort(list, new Comparator<User>() {
					@Override
					public int compare(User one, User other) {
						if (one.getMembership() == null && other.getMembership() == null) {
							return 0;
						}
						if (one.getMembership() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getMembership() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getMembership().isLocked(),
								other.getMembership().isLocked());
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
				Collections.sort(list, new Comparator<User>() {
					@Override
					public int compare(User one, User other) {
						if (one.getMembership() == null && other.getMembership() == null) {
							return 0;
						}
						if (one.getMembership() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getMembership() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getMembership().getEmail(),
								other.getMembership().getEmail());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case PHONE_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<User>() {
					@Override
					public int compare(User one, User other) {
						if (one.getMembership() == null && other.getMembership() == null) {
							return 0;
						}
						if (one.getMembership() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getMembership() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getMembership().getPhone(),
								other.getMembership().getPhone());
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
	protected UserService getService() {
		return userService;
	}
}
