package org.rainbow.asset.explorer.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.ProductReceipt;
import org.rainbow.asset.explorer.service.services.ProductReceiptService;
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
public class ProductReceiptListController extends AbstractNumericIdAuditableListController<ProductReceipt, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8848129878370715630L;

	private static final String REFERENCE_NUMBER_FILTER = "referenceNumber";
	private static final String LOCATION_NAME_FILTER = "location.name";
	private static final String VENDOR_NAME_FILTER = "vendor.name";
	private static final String RECEIPT_DATE_FILTER = "receiptDate";
	private static final String CURRENCY_NAME_FILTER = "currency.name";

	private final SingleValuedFilter<String> referenceNumberFilter;
	private final SingleValuedFilter<String> locationNameFilter;
	private final SingleValuedFilter<String> vendorNameFilter;
	private final SingleValuedFilter<Date> receiptDateFilter;
	private final SingleValuedFilter<String> currencyNameFilter;

	@Autowired
	private ProductReceiptService service;

	public ProductReceiptListController() {
		super(Long.class);
		referenceNumberFilter = new SingleValuedFilter<>(REFERENCE_NUMBER_FILTER, RelationalOperator.CONTAINS, "");
		locationNameFilter = new SingleValuedFilter<>(LOCATION_NAME_FILTER, RelationalOperator.CONTAINS, "");
		vendorNameFilter = new SingleValuedFilter<>(VENDOR_NAME_FILTER, RelationalOperator.CONTAINS, "");
		receiptDateFilter = new SingleValuedFilter<>(RECEIPT_DATE_FILTER, RelationalOperator.EQUAL, new Date());
		currencyNameFilter = new SingleValuedFilter<>(CURRENCY_NAME_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Filterable
	public SingleValuedFilter<String> getReferenceNumberFilter() {
		return referenceNumberFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getLocationNameFilter() {
		return locationNameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getVendorNameFilter() {
		return vendorNameFilter;
	}

	@Filterable
	public SingleValuedFilter<Date> getReceiptDateFilter() {
		return receiptDateFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getCurrencyNameFilter() {
		return currencyNameFilter;
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
	protected Service<ProductReceipt> getService() {
		return service;
	}

}
