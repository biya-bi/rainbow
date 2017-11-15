package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Locale;
import org.rainbow.asset.explorer.service.services.LocaleService;
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
public class LocaleReadController extends AbstractNumericIdAuditableEntityReadController<Locale, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 59389858090861992L;

	private static final String NAME_PATH = "name";
	private static final String LANGUAGE_CODE_PATH = "languageCode";
	private static final String LCID_PATH = "lcid";

	private final StringSearchCriterion nameSearchCriterion;
	private final StringSearchCriterion languageCodeSearchCriterion;
	private final StringSearchCriterion lcidSearchCriterion;

	@Autowired
	private LocaleService service;

	public LocaleReadController() {
		super(Integer.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		languageCodeSearchCriterion = new StringSearchCriterion(LANGUAGE_CODE_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		lcidSearchCriterion = new StringSearchCriterion(LCID_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getLanguageCodeSearchCriterion() {
		return languageCodeSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getLcidSearchCriterion() {
		return lcidSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Locale> list) {
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
			case LANGUAGE_CODE_PATH: {
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
			case LCID_PATH: {
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
	public Service<Locale> getService() {
		return service;
	}

}
