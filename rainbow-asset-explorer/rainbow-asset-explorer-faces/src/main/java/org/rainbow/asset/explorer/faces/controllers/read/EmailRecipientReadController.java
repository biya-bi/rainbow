package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.EmailRecipient;
import org.rainbow.asset.explorer.service.services.EmailRecipientService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;
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
public class EmailRecipientReadController
		extends AbstractNumericIdAuditableEntityReadController<EmailRecipient, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1700034370905504497L;

	private static final String NAME_PATH = "name";
	private static final String EMAIL_PATH = "email";
	private static final String LOCALE_NAME_PATH = "locale.name";

	private final StringSearchCriterion nameSearchCriterion;

	private final StringSearchCriterion emailSearchCriterion;
	private final StringSearchCriterion localeNameSearchCriterion;

	@Autowired
	private EmailRecipientService service;

	public EmailRecipientReadController() {
		super(Integer.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		emailSearchCriterion = new StringSearchCriterion(EMAIL_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		localeNameSearchCriterion = new StringSearchCriterion(LOCALE_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getEmailSearchCriterion() {
		return emailSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getLocaleNameSearchCriterion() {
		return localeNameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<EmailRecipient> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = EMAIL_PATH; // We want to sort by name if no sort
										// field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_PATH: {
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
			case EMAIL_PATH: {
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
			case LOCALE_NAME_PATH: {
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
	public Service<EmailRecipient> getService() {
		return service;
	}

}
