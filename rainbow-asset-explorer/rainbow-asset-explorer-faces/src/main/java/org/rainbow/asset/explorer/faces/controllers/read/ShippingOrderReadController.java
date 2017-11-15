package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
import org.rainbow.asset.explorer.service.services.ShippingOrderService;
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
public class ShippingOrderReadController extends AbstractNumericIdAuditableEntityReadController<ShippingOrder, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1963343851207850184L;

	private final EnumTranslator translator;

	private static final String REFERENCE_NUMBER_PATH = "referenceNumber";
	private static final String SOURCE_LOCATION_NAME_PATH = "sourceLocation.name";
	private static final String TARGET_LOCATION_NAME_PATH = "targetLocation.name";
	private static final String SHIP_METHOD_NAME_PATH = "shipMethod.name";
	private static final String STATUS_PATH = "status";
	private static final String DELIVERY_DATE_PATH = "deliveryDate";
	private static final String SHIP_DATE_PATH = "shipDate";

	private final StringSearchCriterion referenceNumberSearchCriterion;
	private final StringSearchCriterion sourceLocationNameSearchCriterion;
	private final StringSearchCriterion targetLocationNameSearchCriterion;
	private final StringSearchCriterion shipMethodNameSearchCriterion;
	private final StringSearchCriterion statusSearchCriterion;
	private final ComparableSearchCriterion<Date> deliveryDateSearchCriterion;
	private final ComparableSearchCriterion<Date> shipDateSearchCriterion;

	@Autowired
	private ShippingOrderService service;

	public ShippingOrderReadController() {
		super(Long.class);
		translator = new EnumTranslator();

		referenceNumberSearchCriterion = new StringSearchCriterion(REFERENCE_NUMBER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		sourceLocationNameSearchCriterion = new StringSearchCriterion(SOURCE_LOCATION_NAME_PATH, StringSearchCriterion.Operator.CONTAINS,
				null);
		targetLocationNameSearchCriterion = new StringSearchCriterion(TARGET_LOCATION_NAME_PATH, StringSearchCriterion.Operator.CONTAINS,
				null);
		shipMethodNameSearchCriterion = new StringSearchCriterion(SHIP_METHOD_NAME_PATH);
		statusSearchCriterion = new StringSearchCriterion(STATUS_PATH);
		deliveryDateSearchCriterion = new ComparableSearchCriterion<>(DELIVERY_DATE_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
		shipDateSearchCriterion = new ComparableSearchCriterion<>(SHIP_DATE_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
	}

	@SearchCriterion
	public StringSearchCriterion getReferenceNumberSearchCriterion() {
		return referenceNumberSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSourceLocationNameSearchCriterion() {
		return sourceLocationNameSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getDeliveryDateSearchCriterion() {
		return deliveryDateSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getTargetLocationNameSearchCriterion() {
		return targetLocationNameSearchCriterion;
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
	protected void sort(String sortField, SortOrder sortOrder, List<ShippingOrder> list) {
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
				Collections.sort(list, new Comparator<ShippingOrder>() {
					@Override
					public int compare(ShippingOrder one, ShippingOrder other) {
						int result = comparator.compare(one.getReferenceNumber(), other.getReferenceNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SOURCE_LOCATION_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ShippingOrder>() {
					@Override
					public int compare(ShippingOrder one, ShippingOrder other) {
						int result = comparator.compare(one.getSourceLocation().getName(),
								other.getSourceLocation().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case TARGET_LOCATION_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ShippingOrder>() {
					@Override
					public int compare(ShippingOrder one, ShippingOrder other) {
						int result = comparator.compare(one.getTargetLocation().getName(),
								other.getTargetLocation().getName());
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
				Collections.sort(list, new Comparator<ShippingOrder>() {
					@Override
					public int compare(ShippingOrder one, ShippingOrder other) {
						int result = comparator.compare(one.getShipMethod().getName(), other.getShipMethod().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case DELIVERY_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<ShippingOrder>() {
					@Override
					public int compare(ShippingOrder one, ShippingOrder other) {
						int result = comparator.compare(one.getDeliveryDate(), other.getDeliveryDate());
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
				Collections.sort(list, new Comparator<ShippingOrder>() {
					@Override
					public int compare(ShippingOrder one, ShippingOrder other) {
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
				Collections.sort(list, new Comparator<ShippingOrder>() {
					@Override
					public int compare(ShippingOrder one, ShippingOrder other) {
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
	public Service<ShippingOrder> getService() {
		return service;
	}

}
