package org.rainbow.security.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.search.criteria.ComparableSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.services.UserService;
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
public class UserReadController extends AbstractNumericIdAuditableEntityReadController<User, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3766648975654337021L;

	private static final String USER_NAME_PATH = "userName";
	private static final String APPLICATION_NAME_PATH = "application.name";
	private static final String ENABLED_PATH = "membership.enabled";
	private static final String LOCKED_OUT_PATH = "membership.locked";
	private static final String EMAIL_PATH = "membership.email";
	private static final String PHONE_PATH = "membership.phone";

	private final StringSearchCriterion userNameSearchCriterion;
	private final StringSearchCriterion applicationNameSearchCriterion;
	private final ComparableSearchCriterion<Boolean> enabledSearchCriterion;
	private final ComparableSearchCriterion<Boolean> lockedSearchCriterion;
	private final StringSearchCriterion emailSearchCriterion;
	private final StringSearchCriterion phoneSearchCriterion;

	@Autowired
	private UserService userService;

	public UserReadController() {
		super(Long.class);
		userNameSearchCriterion = new StringSearchCriterion(USER_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		applicationNameSearchCriterion = new StringSearchCriterion(APPLICATION_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		enabledSearchCriterion = new ComparableSearchCriterion<>(ENABLED_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
		lockedSearchCriterion = new ComparableSearchCriterion<>(LOCKED_OUT_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
		emailSearchCriterion = new StringSearchCriterion(EMAIL_PATH);
		phoneSearchCriterion = new StringSearchCriterion(PHONE_PATH);
	}

	@SearchCriterion
	public StringSearchCriterion getUserNameSearchCriterion() {
		return userNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getApplicationNameSearchCriterion() {
		return applicationNameSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Boolean> getEnabledSearchCriterion() {
		return enabledSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Boolean> getLockedSearchCriterion() {
		return lockedSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getEmailSearchCriterion() {
		return emailSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getPhoneSearchCriterion() {
		return phoneSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<User> list) {
		if (sortField == null) {
			sortField = USER_NAME_PATH; // We want to sort by user name if no
										// sort field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case USER_NAME_PATH: {
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
			case APPLICATION_NAME_PATH: {
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
			case ENABLED_PATH: {
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
			case LOCKED_OUT_PATH: {
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
			case EMAIL_PATH: {
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
			case PHONE_PATH: {
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
	public Service<User> getService() {
		return userService;
	}
}
