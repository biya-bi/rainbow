package org.rainbow.asset.explorer.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.service.services.LocaleService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.lists.AbstractNumericIdAuditableListController;
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
public class LocaleListController extends AbstractNumericIdAuditableListController<Locale, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 59389858090861992L;

	private static final String NAME_FILTER = "name";
	private static final String LANGUAGE_CODE_FILTER = "languageCode";
	private static final String LCID_FILTER = "lcid";

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> languageCodeFilter;
	private final SingleValuedFilter<String> lcidFilter;

	@Autowired
	private LocaleService service;

	public LocaleListController() {
		super(Integer.class);
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		languageCodeFilter = new SingleValuedFilter<>(LANGUAGE_CODE_FILTER, RelationalOperator.CONTAINS, "");
		lcidFilter = new SingleValuedFilter<>(LCID_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Filterable
	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getLanguageCodeFilter() {
		return languageCodeFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getLcidFilter() {
		return lcidFilter;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Locale> list) {
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
				Collections.sort(list, new Comparator<Locale>() {
					@Override
					public int compare(Locale one, Locale other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LANGUAGE_CODE_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Locale>() {
					@Override
					public int compare(Locale one, Locale other) {
						int result = comparator.compare(one.getLanguageCode(), other.getLanguageCode());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LCID_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Locale>() {
					@Override
					public int compare(Locale one, Locale other) {
						int result = comparator.compare(one.getLcid(), other.getLcid());
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
	protected Service<Locale> getService() {
		return service;
	}

}
