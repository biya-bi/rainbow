package org.rainbow.asset.explorer.faces.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.EmailTemplate;
import org.rainbow.asset.explorer.service.services.EmailTemplateService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.Filterable;
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
public class EmailTemplateLazyDataModel extends IntegerIdTrackableLazyDataModel<EmailTemplate> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7950941901606366069L;

	private static final String NAME_FILTER = "name";
	private static final String SUBJECT_FILTER = "subject";
	private static final String LOCALE_NAME_FILTER = "locale.name";

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> subjectFilter;
	private final SingleValuedFilter<String> localeNameFilter;

	@Autowired
	private EmailTemplateService service;

	public EmailTemplateLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		subjectFilter = new SingleValuedFilter<>(SUBJECT_FILTER, RelationalOperator.CONTAINS, "");
		localeNameFilter = new SingleValuedFilter<>(LOCALE_NAME_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Filterable
	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getSubjectFilter() {
		return subjectFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getLocaleNameFilter() {
		return localeNameFilter;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<EmailTemplate> list) {
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
			case SUBJECT_FILTER: {
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
			case LOCALE_NAME_FILTER: {
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
	protected Service<EmailTemplate> getService() {
		return service;
	}

}
