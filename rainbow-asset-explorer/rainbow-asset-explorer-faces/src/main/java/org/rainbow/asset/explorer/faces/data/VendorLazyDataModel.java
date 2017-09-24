package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.persistence.Filter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class VendorLazyDataModel extends LongIdTrackableLazyDataModel<Vendor> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7448646464760524071L;

	private static final String NAME_FILTER = "name";
	private static final String ACCOUNT_NUMBER_FILTER = "accountNumber";
	private static final String PURCHASING_URL_FILTER = "purchasingUrl";
	private static final String ACTIVE_FILTER = "active";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> accountNumberFilter;
	private final SingleValuedFilter<String> purchasingUrlFilter;
	private final SingleValuedFilter<Boolean> activeFilter;

	@Autowired
	@Qualifier("vendorService")
	private Service<Vendor, Long, SearchOptions> service;
	
	public VendorLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		accountNumberFilter = new SingleValuedFilter<>(ACCOUNT_NUMBER_FILTER, RelationalOperator.CONTAINS, "");
		purchasingUrlFilter = new SingleValuedFilter<>(PURCHASING_URL_FILTER, RelationalOperator.CONTAINS, "");
		activeFilter = new SingleValuedFilter<>(ACTIVE_FILTER, RelationalOperator.EQUAL, null);

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(accountNumberFilter);
		filters.add(purchasingUrlFilter);
		filters.add(activeFilter);
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getAccountNumberFilter() {
		return accountNumberFilter;
	}

	public SingleValuedFilter<String> getPurchasingUrlFilter() {
		return purchasingUrlFilter;
	}

	public SingleValuedFilter<Boolean> getActiveFilter() {
		return activeFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		filters.remove(activeFilter);
		if (activeFilter.getValue() != null)
			filters.add(activeFilter);
		List<Filter<?>> baseFilters = super.getFilters();
		if (baseFilters != null) {
			ArrayList<Filter<?>> combinedFilters = new ArrayList<>(baseFilters);
			combinedFilters.addAll(filters);
			return combinedFilters;
		}
		return filters;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Vendor> list) {
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
					Collections.sort(list, new Comparator<Vendor>() {
						@Override
						public int compare(Vendor one, Vendor other) {
							int result = comparator.compare(one.getName(), other.getName());
							if (order == SortOrder.DESCENDING) {
								return -result;
							}
							return result;
						}
					});
					break;
				}
				case ACCOUNT_NUMBER_FILTER: {
					final Comparator<String> comparator = DefaultComparator.<String>getInstance();
					Collections.sort(list, new Comparator<Vendor>() {
						@Override
						public int compare(Vendor one, Vendor other) {
							int result = comparator.compare(one.getAccountNumber(), other.getAccountNumber());
							if (order == SortOrder.DESCENDING) {
								return -result;
							}
							return result;
						}
					});
					break;
				}
				case PURCHASING_URL_FILTER: {
					final Comparator<String> comparator = DefaultComparator.<String>getInstance();
					Collections.sort(list, new Comparator<Vendor>() {
						@Override
						public int compare(Vendor one, Vendor other) {
							int result = comparator.compare(one.getPurchasingUrl(), other.getPurchasingUrl());
							if (order == SortOrder.DESCENDING) {
								return -result;
							}
							return result;
						}
					});
					break;
				}
				case ACTIVE_FILTER: {
					final Comparator<Boolean> comparator = DefaultComparator.<Boolean>getInstance();
					Collections.sort(list, new Comparator<Vendor>() {
						@Override
						public int compare(Vendor one, Vendor other) {
							int result = comparator.compare(one.isActive(), other.isActive());
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
	protected Service<Vendor, Long, SearchOptions> getService() {
		return service;
	}

}
