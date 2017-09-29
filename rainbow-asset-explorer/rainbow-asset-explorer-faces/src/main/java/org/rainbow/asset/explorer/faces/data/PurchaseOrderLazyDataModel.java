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
import org.rainbow.asset.explorer.orm.entities.PurchaseOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.persistence.Filter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.persistence.SingleValuedFilter;
import org.rainbow.service.services.Service;
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
public class PurchaseOrderLazyDataModel extends LongIdTrackableLazyDataModel<PurchaseOrder> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2841483114764560310L;

    private final EnumTranslator translator;

    private static final String REFERENCE_NUMBER_FILTER = "referenceNumber";
    private static final String VENDOR_NAME_FILTER = "vendor.name";
    private static final String LOCATION_NAME_FILTER = "location.name";
    private static final String SHIP_METHOD_NAME_FILTER = "shipMethod.name";
    private static final String STATUS_FILTER = "status";
    private static final String TAX_AMOUNT_FILTER = "taxAmount";
    private static final String REVISION_NUMBER_FILTER = "revisionNumber";
    private static final String FREIGHT_FILTER = "freight";
    private static final String SHIP_DATE_FILTER = "shipDate";

    private final List<Filter<?>> filters;

    private final SingleValuedFilter<String> referenceNumberFilter;
    private final SingleValuedFilter<String> vendorNameFilter;
    private final SingleValuedFilter<String> locationNameFilter;
    private final SingleValuedFilter<String> shipMethodNameFilter;
    private final SingleValuedFilter<String> statusFilter;
    private final SingleValuedFilter<String> taxAmountFilter;
    private final SingleValuedFilter<String> revisionNumberFilter;
    private final SingleValuedFilter<String> freightFilter;
    private final SingleValuedFilter<String> shipDateFilter;

	@Autowired
	@Qualifier("purchaseOrderService")
	private Service<PurchaseOrder, Long, SearchOptions> service;
	
    public PurchaseOrderLazyDataModel() {
        translator = new EnumTranslator();

        referenceNumberFilter = new SingleValuedFilter<>(REFERENCE_NUMBER_FILTER, RelationalOperator.CONTAINS, "");
        vendorNameFilter = new SingleValuedFilter<>(VENDOR_NAME_FILTER, RelationalOperator.CONTAINS, "");
        locationNameFilter = new SingleValuedFilter<>(LOCATION_NAME_FILTER);
        shipMethodNameFilter = new SingleValuedFilter<>(SHIP_METHOD_NAME_FILTER);
        statusFilter = new SingleValuedFilter<>(STATUS_FILTER);
        taxAmountFilter = new SingleValuedFilter<>(TAX_AMOUNT_FILTER);
        revisionNumberFilter = new SingleValuedFilter<>(REVISION_NUMBER_FILTER);
        freightFilter = new SingleValuedFilter<>(FREIGHT_FILTER);
        shipDateFilter = new SingleValuedFilter<>(SHIP_DATE_FILTER, RelationalOperator.EQUAL, null);

        filters = new ArrayList<>();
        filters.add(referenceNumberFilter);
        filters.add(vendorNameFilter);
        filters.add(locationNameFilter);
        filters.add(shipMethodNameFilter);
        filters.add(statusFilter);
        filters.add(taxAmountFilter);
        filters.add(revisionNumberFilter);
        filters.add(freightFilter);
        filters.add(shipDateFilter);
    }

    public SingleValuedFilter<String> getReferenceNumberFilter() {
        return referenceNumberFilter;
    }

    public SingleValuedFilter<String> getVendorNameFilter() {
        return vendorNameFilter;
    }

    public SingleValuedFilter<String> getTaxAmountFilter() {
        return taxAmountFilter;
    }

    public SingleValuedFilter<String> getRevisionNumberFilter() {
        return revisionNumberFilter;
    }

    public SingleValuedFilter<String> getFreightFilter() {
        return freightFilter;
    }

    public SingleValuedFilter<String> getLocationNameFilter() {
        return locationNameFilter;
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
    protected void sort(String sortField, SortOrder sortOrder, List<PurchaseOrder> list) {
        super.sort(sortField, sortOrder, list);
        if (sortField == null) {
            sortField = REFERENCE_NUMBER_FILTER; // We want to sort by reference number if no sort field was specified.
        }
        final SortOrder order = sortOrder;
        if (null != sortField) {
            switch (sortField) {
                case REFERENCE_NUMBER_FILTER: {
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
                case VENDOR_NAME_FILTER: {
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
                case LOCATION_NAME_FILTER: {
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
                case SHIP_METHOD_NAME_FILTER: {
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
                case TAX_AMOUNT_FILTER: {
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
                case REVISION_NUMBER_FILTER: {
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
                case FREIGHT_FILTER: {
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
                case SHIP_DATE_FILTER: {
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
                case STATUS_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<PurchaseOrder>() {
                        @Override
                        public int compare(PurchaseOrder one, PurchaseOrder other) {
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
	protected Service<PurchaseOrder, Long, SearchOptions> getService() {
		return service;
	}

}
