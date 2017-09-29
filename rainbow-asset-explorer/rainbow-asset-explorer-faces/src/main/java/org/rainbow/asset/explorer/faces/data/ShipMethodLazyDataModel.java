package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.ShipMethod;
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
public class ShipMethodLazyDataModel extends LongIdTrackableLazyDataModel<ShipMethod> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9083244946398015256L;


    private static final String NAME_FILTER = "name";

    private final List<Filter<?>> filters;

    private final SingleValuedFilter<String> nameFilter;

	@Autowired
	@Qualifier("shipMethodService")
	private Service<ShipMethod, Long, SearchOptions> service;
	
    public ShipMethodLazyDataModel() {
        nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");

        filters = new ArrayList<>();
        filters.add(nameFilter);
    }

    public SingleValuedFilter<String> getNameFilter() {
        return nameFilter;
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
    protected void sort(String sortField, SortOrder sortOrder, List<ShipMethod> list) {
        super.sort(sortField, sortOrder, list);
        if (sortField == null) {
            sortField = NAME_FILTER; // We want to sort by name if no sort field was specified.
        }
        final SortOrder order = sortOrder;
        if (null != sortField) {
            switch (sortField) {
                case NAME_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<ShipMethod>() {
                        @Override
                        public int compare(ShipMethod one, ShipMethod other) {
                            int result = comparator.compare(one.getName(), other.getName());
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
	protected Service<ShipMethod, Long, SearchOptions> getService() {
		return service;
	}

}
