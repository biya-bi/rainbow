package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.ProductIssue;
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
public class ProductIssueLazyDataModel extends LongIdTrackableLazyDataModel<ProductIssue> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5288934312401804012L;

    private static final String REFERENCE_NUMBER_FILTER = "referenceNumber";
    private static final String LOCATION_NAME_FILTER = "location.name";
    private static final String REQUISITIONER_FILTER = "requisitioner";
    private static final String ISSUE_DATE_FILTER = "issueDate";
    private static final String DEPARTMENT_NAME_FILTER = "department.name";

    private final List<Filter<?>> filters;

    private final SingleValuedFilter<String> referenceNumberFilter;
    private final SingleValuedFilter<String> locationNameFilter;
    private final SingleValuedFilter<String> requisitionerFilter;
    private final SingleValuedFilter<Date> issueDateFilter;
    private final SingleValuedFilter<String> departmentNameFilter;

	@Autowired
	@Qualifier("productIssueService")
	private Service<ProductIssue, Long, SearchOptions> service;
	
    public ProductIssueLazyDataModel() {

        referenceNumberFilter = new SingleValuedFilter<>(REFERENCE_NUMBER_FILTER, RelationalOperator.CONTAINS, "");
        locationNameFilter = new SingleValuedFilter<>(LOCATION_NAME_FILTER, RelationalOperator.CONTAINS, "");
        requisitionerFilter = new SingleValuedFilter<>(REQUISITIONER_FILTER, RelationalOperator.CONTAINS, "");
        issueDateFilter = new SingleValuedFilter<>(ISSUE_DATE_FILTER, RelationalOperator.EQUAL, new Date());
        departmentNameFilter = new SingleValuedFilter<>(DEPARTMENT_NAME_FILTER, RelationalOperator.CONTAINS, "");

        filters = new ArrayList<>();
        filters.add(referenceNumberFilter);
        filters.add(locationNameFilter);
        filters.add(requisitionerFilter);
        filters.add(issueDateFilter);
        filters.add(departmentNameFilter);
    }

    public SingleValuedFilter<String> getReferenceNumberFilter() {
        return referenceNumberFilter;
    }

    public SingleValuedFilter<String> getLocationNameFilter() {
        return locationNameFilter;
    }

    public SingleValuedFilter<String> getRequisitionerFilter() {
        return requisitionerFilter;
    }

    public SingleValuedFilter<Date> getIssueDateFilter() {
        return issueDateFilter;
    }

    public SingleValuedFilter<String> getDepartmentNameFilter() {
        return departmentNameFilter;
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
    protected void sort(String sortField, SortOrder sortOrder, List<ProductIssue> list) {
        super.sort(sortField, sortOrder, list);
        if (sortField == null) {
            sortField = REFERENCE_NUMBER_FILTER; // We want to sort by reference number if no sort field was specified.
        }
        final SortOrder order = sortOrder;
        if (null != sortField) {
            switch (sortField) {
                case REFERENCE_NUMBER_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<ProductIssue>() {
                        @Override
                        public int compare(ProductIssue one, ProductIssue other) {
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
                    Collections.sort(list, new Comparator<ProductIssue>() {
                        @Override
                        public int compare(ProductIssue one, ProductIssue other) {
                            int result = comparator.compare(one.getLocation().getName(), other.getLocation().getName());
                            if (order == SortOrder.DESCENDING) {
                                return -result;
                            }
                            return result;
                        }
                    });
                    break;
                }
                case REQUISITIONER_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<ProductIssue>() {
                        @Override
                        public int compare(ProductIssue one, ProductIssue other) {
                            int result = comparator.compare(one.getRequisitioner(), other.getRequisitioner());
                            if (order == SortOrder.DESCENDING) {
                                return -result;
                            }
                            return result;
                        }
                    });
                    break;
                }
                case DEPARTMENT_NAME_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<ProductIssue>() {
                        @Override
                        public int compare(ProductIssue one, ProductIssue other) {
                            if (one.getDepartment() == null && other.getDepartment() == null) {
                                return 0;
                            }
                            if (one.getDepartment() == null) {
                                return order == SortOrder.DESCENDING ? 1 : -1;
                            }
                            if (other.getDepartment() == null) {
                                return order == SortOrder.DESCENDING ? -1 : 1;
                            }
                            int result = comparator.compare(one.getDepartment().getName(), other.getDepartment().getName());
                            if (order == SortOrder.DESCENDING) {
                                return -result;
                            }
                            return result;
                        }
                    });
                    break;
                }
                case ISSUE_DATE_FILTER: {
                    final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
                    Collections.sort(list, new Comparator<ProductIssue>() {
                        @Override
                        public int compare(ProductIssue one, ProductIssue other) {
                            int result = comparator.compare(one.getIssueDate(), other.getIssueDate());
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
	protected Service<ProductIssue, Long, SearchOptions> getService() {
		return service;
	}

}
