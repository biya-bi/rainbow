package org.rainbow.asset.explorer.faces.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Currency;
import org.rainbow.asset.explorer.service.services.CurrencyService;
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
public class CurrencyLazyDataModel extends IntegerIdTrackableLazyDataModel<Currency> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4327441980548488842L;

	private static final String NAME_FILTER = "name";
	private static final String SYMBOE_FILTER = "symbol";

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> symbolFilter;

	@Autowired
	private CurrencyService service;

	public CurrencyLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		symbolFilter = new SingleValuedFilter<>(SYMBOE_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Filterable
	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getSymbolFilter() {
		return symbolFilter;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Currency> list) {
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
			case SYMBOE_FILTER: {
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
	protected Service<Currency> getService() {
		return service;
	}

}
