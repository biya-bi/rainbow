package org.rainbow.asset.explorer.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.service.services.EmailRecipientService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.lists.AbstractNumericIdAuditableEntityListController;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.Filterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class EmailRecipientListController extends AbstractNumericIdAuditableEntityListController<EmailRecipient, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1700034370905504497L;

	private static final String NAME_FILTER = "name";
	private static final String EMAIL_FILTER = "email";
	private static final String LOCALE_NAME_FILTER = "locale.name";

	private final SingleValuedFilter<String> nameFilter;

	private final SingleValuedFilter<String> emailFilter;
	private final SingleValuedFilter<String> localeNameFilter;

	@Autowired
	private EmailRecipientService service;

	public EmailRecipientListController() {
		super(Integer.class);
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		emailFilter = new SingleValuedFilter<>(EMAIL_FILTER, RelationalOperator.CONTAINS, "");
		localeNameFilter = new SingleValuedFilter<>(LOCALE_NAME_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Filterable
	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getEmailFilter() {
		return emailFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getLocaleNameFilter() {
		return localeNameFilter;
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
	protected EmailRecipientService getService() {
		return service;
	}

}
