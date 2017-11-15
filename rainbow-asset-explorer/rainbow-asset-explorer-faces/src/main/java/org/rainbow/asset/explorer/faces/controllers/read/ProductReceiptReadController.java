package org.rainbow.asset.explorer.faces.controllers.read;

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
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.search.criteria.ComparableSearchCriterion;
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
public class ProductReceiptReadController extends AbstractNumericIdAuditableEntityReadController<ProductReceipt, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8848129878370715630L;

	private static final String REFERENCE_NUMBER_PATH = "referenceNumber";
	private static final String LOCATION_NAME_PATH = "location.name";
	private static final String VENDOR_NAME_PATH = "vendor.name";
	private static final String RECEIPT_DATE_PATH = "receiptDate";
	private static final String CURRENCY_NAME_PATH = "currency.name";

	private final StringSearchCriterion referenceNumberSearchCriterion;
	private final StringSearchCriterion locationNameSearchCriterion;
	private final StringSearchCriterion vendorNameSearchCriterion;
	private final ComparableSearchCriterion<Date> receiptDateSearchCriterion;
	private final StringSearchCriterion currencyNameSearchCriterion;

	@Autowired
	private ProductReceiptService service;

	public ProductReceiptReadController() {
		super(Long.class);
		referenceNumberSearchCriterion = new StringSearchCriterion(REFERENCE_NUMBER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		locationNameSearchCriterion = new StringSearchCriterion(LOCATION_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		vendorNameSearchCriterion = new StringSearchCriterion(VENDOR_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		receiptDateSearchCriterion = new ComparableSearchCriterion<>(RECEIPT_DATE_PATH, ComparableSearchCriterion.Operator.EQUAL, new Date());
		currencyNameSearchCriterion = new StringSearchCriterion(CURRENCY_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getReferenceNumberSearchCriterion() {
		return referenceNumberSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getLocationNameSearchCriterion() {
		return locationNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getVendorNameSearchCriterion() {
		return vendorNameSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getReceiptDateSearchCriterion() {
		return receiptDateSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getCurrencyNameSearchCriterion() {
		return currencyNameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<ProductReceipt> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = REFERENCE_NUMBER_PATH; // We want to sort by reference
													// number if no sort field
													// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case REFERENCE_NUMBER_PATH: {
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
			case LOCATION_NAME_PATH: {
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
			case VENDOR_NAME_PATH: {
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
			case CURRENCY_NAME_PATH: {
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
			case RECEIPT_DATE_PATH: {
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
	public Service<ProductReceipt> getService() {
		return service;
	}

}
