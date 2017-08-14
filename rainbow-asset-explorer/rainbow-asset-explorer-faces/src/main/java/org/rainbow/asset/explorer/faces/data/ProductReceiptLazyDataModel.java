/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.core.entities.ProductReceipt;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.service.Service;
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
public class ProductReceiptLazyDataModel extends LongIdTrackableLazyDataModel<ProductReceipt> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8848129878370715630L;

	private static final String REFERENCE_NUMBER_FILTER = "referenceNumber";
	private static final String LOCATION_NAME_FILTER = "location.name";
	private static final String VENDOR_NAME_FILTER = "vendor.name";
	private static final String RECEIPT_DATE_FILTER = "receiptDate";
	private static final String CURRENCY_NAME_FILTER = "currency.name";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> referenceNumberFilter;
	private final SingleValuedFilter<String> locationNameFilter;
	private final SingleValuedFilter<String> vendorNameFilter;
	private final SingleValuedFilter<Date> receiptDateFilter;
	private final SingleValuedFilter<String> currencyNameFilter;

	@Autowired
	@Qualifier("productReceiptService")
	private Service<ProductReceipt, Long, SearchOptions> service;

	public ProductReceiptLazyDataModel() {

		referenceNumberFilter = new SingleValuedFilter<>(REFERENCE_NUMBER_FILTER, RelationalOperator.CONTAINS, "");
		locationNameFilter = new SingleValuedFilter<>(LOCATION_NAME_FILTER, RelationalOperator.CONTAINS, "");
		vendorNameFilter = new SingleValuedFilter<>(VENDOR_NAME_FILTER, RelationalOperator.CONTAINS, "");
		receiptDateFilter = new SingleValuedFilter<>(RECEIPT_DATE_FILTER, RelationalOperator.EQUAL, new Date());
		currencyNameFilter = new SingleValuedFilter<>(CURRENCY_NAME_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(referenceNumberFilter);
		filters.add(locationNameFilter);
		filters.add(vendorNameFilter);
		filters.add(receiptDateFilter);
		filters.add(currencyNameFilter);
	}

	public SingleValuedFilter<String> getReferenceNumberFilter() {
		return referenceNumberFilter;
	}

	public SingleValuedFilter<String> getLocationNameFilter() {
		return locationNameFilter;
	}

	public SingleValuedFilter<String> getVendorNameFilter() {
		return vendorNameFilter;
	}

	public SingleValuedFilter<Date> getReceiptDateFilter() {
		return receiptDateFilter;
	}

	public SingleValuedFilter<String> getCurrencyNameFilter() {
		return currencyNameFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		List<Filter<?>> baseFilters = super.getFilters();
		if (baseFilters != null) {
			ArrayList<Filter<?>> combinedFilters = new ArrayList<>(baseFilters);
			combinedFilters.addAll(filters);
			return combinedFilters;
		}
		return filters;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<ProductReceipt> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = REFERENCE_NUMBER_FILTER; // We want to sort by reference
													// number if no sort field
													// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case REFERENCE_NUMBER_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductReceipt>() {
					@Override
					public int compare(ProductReceipt one, ProductReceipt other) {
						int result = comparator.compare(one.getReferenceNumber(), other.getReferenceNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LOCATION_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductReceipt>() {
					@Override
					public int compare(ProductReceipt one, ProductReceipt other) {
						int result = comparator.compare(one.getLocation().getName(), other.getLocation().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case VENDOR_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductReceipt>() {
					@Override
					public int compare(ProductReceipt one, ProductReceipt other) {
						int result = comparator.compare(one.getVendor().getName(), other.getVendor().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case CURRENCY_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductReceipt>() {
					@Override
					public int compare(ProductReceipt one, ProductReceipt other) {
						int result = comparator.compare(one.getCurrency().getName(), other.getCurrency().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case RECEIPT_DATE_FILTER: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<ProductReceipt>() {
					@Override
					public int compare(ProductReceipt one, ProductReceipt other) {
						int result = comparator.compare(one.getReceiptDate(), other.getReceiptDate());
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
	protected Service<ProductReceipt, Long, SearchOptions> getService() {
		return service;
	}

}
