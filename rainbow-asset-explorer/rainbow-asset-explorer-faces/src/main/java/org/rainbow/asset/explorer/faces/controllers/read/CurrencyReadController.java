package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.service.services.CurrencyService;
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
public class CurrencyReadController extends AbstractNumericIdAuditableEntityReadController<Currency, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4327441980548488842L;

	private static final String NAME_PATH = "name";
	private static final String SYMBOE_PATH = "symbol";

	private final StringSearchCriterion nameSearchCriterion;
	private final StringSearchCriterion symbolSearchCriterion;

	@Autowired
	private CurrencyService service;

	public CurrencyReadController() {
		super(Integer.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		symbolSearchCriterion = new StringSearchCriterion(SYMBOE_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSymbolSearchCriterion() {
		return symbolSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Currency> list) {
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
				Collections.sort(list, new Comparator<Currency>() {
					@Override
					public int compare(Currency one, Currency other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SYMBOE_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Currency>() {
					@Override
					public int compare(Currency one, Currency other) {
						int result = comparator.compare(one.getSymbol(), other.getSymbol());
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
	public Service<Currency> getService() {
		return service;
	}

}
