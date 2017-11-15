package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrder;
import org.rainbow.asset.explorer.service.services.PurchaseOrderService;
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
public class PurchaseOrderReadController extends AbstractNumericIdAuditableEntityReadController<PurchaseOrder, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2841483114764560310L;

	private final EnumTranslator translator;

	private static final String REFERENCE_NUMBER_PATH = "referenceNumber";
	private static final String VENDOR_NAME_PATH = "vendor.name";
	private static final String LOCATION_NAME_PATH = "location.name";
	private static final String SHIP_METHOD_NAME_PATH = "shipMethod.name";
	private static final String STATUS_PATH = "status";
	private static final String TAX_AMOUNT_PATH = "taxAmount";
	private static final String REVISION_NUMBER_PATH = "revisionNumber";
	private static final String FREIGHT_PATH = "freight";
	private static final String SHIP_DATE_PATH = "shipDate";

	private final StringSearchCriterion referenceNumberSearchCriterion;
	private final StringSearchCriterion vendorNameSearchCriterion;
	private final StringSearchCriterion locationNameSearchCriterion;
	private final StringSearchCriterion shipMethodNameSearchCriterion;
	private final StringSearchCriterion statusSearchCriterion;
	private final ComparableSearchCriterion<Double> taxAmountSearchCriterion;
	private final ComparableSearchCriterion<Byte> revisionNumberSearchCriterion;
	private final ComparableSearchCriterion<Double> freightSearchCriterion;
	private final ComparableSearchCriterion<Date> shipDateSearchCriterion;

	@Autowired
	private PurchaseOrderService service;

	public PurchaseOrderReadController() {
		super(Long.class);
		translator = new EnumTranslator();

		referenceNumberSearchCriterion = new StringSearchCriterion(REFERENCE_NUMBER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		vendorNameSearchCriterion = new StringSearchCriterion(VENDOR_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		locationNameSearchCriterion = new StringSearchCriterion(LOCATION_NAME_PATH);
		shipMethodNameSearchCriterion = new StringSearchCriterion(SHIP_METHOD_NAME_PATH);
		statusSearchCriterion = new StringSearchCriterion(STATUS_PATH);
		taxAmountSearchCriterion = new ComparableSearchCriterion<>(TAX_AMOUNT_PATH);
		revisionNumberSearchCriterion = new ComparableSearchCriterion<>(REVISION_NUMBER_PATH);
		freightSearchCriterion = new ComparableSearchCriterion<>(FREIGHT_PATH);
		shipDateSearchCriterion = new ComparableSearchCriterion<>(SHIP_DATE_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
	}

	@SearchCriterion
	public StringSearchCriterion getReferenceNumberSearchCriterion() {
		return referenceNumberSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getVendorNameSearchCriterion() {
		return vendorNameSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Double> getTaxAmountSearchCriterion() {
		return taxAmountSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Byte> getRevisionNumberSearchCriterion() {
		return revisionNumberSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Double> getFreightSearchCriterion() {
		return freightSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getLocationNameSearchCriterion() {
		return locationNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getShipMethodNameSearchCriterion() {
		return shipMethodNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getStatusSearchCriterion() {
		return statusSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getShipDateSearchCriterion() {
		return shipDateSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<PurchaseOrder> list) {
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
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						int result = comparator.compare(one.getReferenceNumber(), other.getReferenceNumber());
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
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						int result = comparator.compare(one.getVendor().getName(), other.getVendor().getName());
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
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						if (one.getLocation() == null && other.getLocation() == null) {
							return 0;
						}
						if (one.getLocation() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getLocation() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getLocation().getName(), other.getLocation().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SHIP_METHOD_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						if (one.getShipMethod() == null && other.getShipMethod() == null) {
							return 0;
						}
						if (one.getShipMethod() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getShipMethod() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getShipMethod().getName(), other.getShipMethod().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case TAX_AMOUNT_PATH: {
				final Comparator<Double> comparator = DefaultComparator.<Double>getInstance();
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						int result = comparator.compare(one.getTaxAmount(), other.getTaxAmount());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case REVISION_NUMBER_PATH: {
				final Comparator<Byte> comparator = DefaultComparator.<Byte>getInstance();
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						int result = comparator.compare(one.getRevisionNumber(), other.getRevisionNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case FREIGHT_PATH: {
				final Comparator<Double> comparator = DefaultComparator.<Double>getInstance();
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						int result = comparator.compare(one.getFreight(), other.getFreight());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SHIP_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						int result = comparator.compare(one.getShipDate(), other.getShipDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case STATUS_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<PurchaseOrder>() {
					@Override
					public int compare(PurchaseOrder one, PurchaseOrder other) {
						int result = comparator.compare(translator.translate(one.getStatus()),
								translator.translate(other.getStatus()));
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
	public Service<PurchaseOrder> getService() {
		return service;
	}

}
