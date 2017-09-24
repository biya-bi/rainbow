package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.ShippingOrder;
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
public class ShippingOrderLazyDataModel extends LongIdTrackableLazyDataModel<ShippingOrder> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1963343851207850184L;

    private final EnumTranslator translator;

    private static final String REFERENCE_NUMBER_FILTER = "referenceNumber";
    private static final String SOURCE_LOCATION_NAME_FILTER = "sourceLocation.name";
    private static final String TARGET_LOCATION_NAME_FILTER = "targetLocation.name";
    private static final String SHIP_METHOD_NAME_FILTER = "shipMethod.name";
    private static final String STATUS_FILTER = "status";
    private static final String DELIVERY_DATE_FILTER = "deliveryDate";
    private static final String SHIP_DATE_FILTER = "shipDate";

    private final List<Filter<?>> filters;

    private final SingleValuedFilter<String> referenceNumberFilter;
    private final SingleValuedFilter<String> sourceLocationNameFilter;
    private final SingleValuedFilter<String> targetLocationNameFilter;
    private final SingleValuedFilter<String> shipMethodNameFilter;
    private final SingleValuedFilter<String> statusFilter;
    private final SingleValuedFilter<String> deliveryDateFilter;
    private final SingleValuedFilter<String> shipDateFilter;

	@Autowired
	@Qualifier("shippingOrderService")
	private Service<ShippingOrder, Long, SearchOptions> service;
	
    public ShippingOrderLazyDataModel() {
        translator = new EnumTranslator();

        referenceNumberFilter = new SingleValuedFilter<>(REFERENCE_NUMBER_FILTER, RelationalOperator.CONTAINS, "");
        sourceLocationNameFilter = new SingleValuedFilter<>(SOURCE_LOCATION_NAME_FILTER, RelationalOperator.CONTAINS, "");
        targetLocationNameFilter = new SingleValuedFilter<>(TARGET_LOCATION_NAME_FILTER, RelationalOperator.CONTAINS, "");
        shipMethodNameFilter = new SingleValuedFilter<>(SHIP_METHOD_NAME_FILTER);
        statusFilter = new SingleValuedFilter<>(STATUS_FILTER);
        deliveryDateFilter = new SingleValuedFilter<>(DELIVERY_DATE_FILTER, RelationalOperator.EQUAL, null);
        shipDateFilter = new SingleValuedFilter<>(SHIP_DATE_FILTER, RelationalOperator.EQUAL, null);

        filters = new ArrayList<>();
        filters.add(referenceNumberFilter);
        filters.add(sourceLocationNameFilter);
        filters.add(targetLocationNameFilter);
        filters.add(shipMethodNameFilter);
        filters.add(statusFilter);
        filters.add(deliveryDateFilter);
        filters.add(shipDateFilter);
    }

    public SingleValuedFilter<String> getReferenceNumberFilter() {
        return referenceNumberFilter;
    }

    public SingleValuedFilter<String> getSourceLocationNameFilter() {
        return sourceLocationNameFilter;
    }

    public SingleValuedFilter<String> getDeliveryDateFilter() {
        return deliveryDateFilter;
    }

    public SingleValuedFilter<String> getTargetLocationNameFilter() {
        return targetLocationNameFilter;
    }

    public SingleValuedFilter<String> getShipMethodNameFilter() {
        return shipMethodNameFilter;
    }

    public SingleValuedFilter<String> getStatusFilter() {
        return statusFilter;
    }

    public SingleValuedFilter<String> getShipDateFilter() {
        return shipDateFilter;
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
    protected void sort(String sortField, SortOrder sortOrder, List<ShippingOrder> list) {
        super.sort(sortField, sortOrder, list);
        if (sortField == null) {
            sortField = REFERENCE_NUMBER_FILTER; // We want to sort by reference number if no sort field was specified.
        }
        final SortOrder order = sortOrder;
        if (null != sortField) {
            switch (sortField) {
                case REFERENCE_NUMBER_FILTER: {
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
                case SOURCE_LOCATION_NAME_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<ShippingOrder>() {
                        @Override
                        public int compare(ShippingOrder one, ShippingOrder other) {
                            int result = comparator.compare(one.getSourceLocation().getName(), other.getSourceLocation().getName());
                            if (order == SortOrder.DESCENDING) {
                                return -result;
                            }
                            return result;
                        }
                    });
                    break;
                }
                case TARGET_LOCATION_NAME_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<ShippingOrder>() {
                        @Override
                        public int compare(ShippingOrder one, ShippingOrder other) {
                            int result = comparator.compare(one.getTargetLocation().getName(), other.getTargetLocation().getName());
                            if (order == SortOrder.DESCENDING) {
                                return -result;
                            }
                            return result;
                        }
                    });
                    break;
                }
                case SHIP_METHOD_NAME_FILTER: {
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
                case DELIVERY_DATE_FILTER: {
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
                case SHIP_DATE_FILTER: {
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
                case STATUS_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<ShippingOrder>() {
                        @Override
                        public int compare(ShippingOrder one, ShippingOrder other) {
                            int result = comparator.compare(translator.translate(one.getStatus()), translator.translate(other.getStatus()));
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
	protected Service<ShippingOrder, Long, SearchOptions> getService() {
		return service;
	}

}
