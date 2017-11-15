package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.service.services.EmailTemplateService;
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
public class EmailTemplateReadController extends AbstractNumericIdAuditableEntityReadController<EmailTemplate, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7950941901606366069L;

	private static final String NAME_PATH = "name";
	private static final String SUBJECT_PATH = "subject";
	private static final String LOCALE_NAME_PATH = "locale.name";

	private final StringSearchCriterion nameSearchCriterion;
	private final StringSearchCriterion subjectSearchCriterion;
	private final StringSearchCriterion localeNameSearchCriterion;

	@Autowired
	private EmailTemplateService service;

	public EmailTemplateReadController() {
		super(Integer.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		subjectSearchCriterion = new StringSearchCriterion(SUBJECT_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		localeNameSearchCriterion = new StringSearchCriterion(LOCALE_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSubjectSearchCriterion() {
		return subjectSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getLocaleNameSearchCriterion() {
		return localeNameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<EmailTemplate> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = NAME_PATH; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<EmailTemplate>() {
					@Override
					public int compare(EmailTemplate one, EmailTemplate other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SUBJECT_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<EmailTemplate>() {
					@Override
					public int compare(EmailTemplate one, EmailTemplate other) {
						int result = comparator.compare(one.getSubject(), other.getSubject());
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
				Collections.sort(list, new Comparator<EmailTemplate>() {
					@Override
					public int compare(EmailTemplate one, EmailTemplate other) {
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
	public Service<EmailTemplate> getService() {
		return service;
	}

}
