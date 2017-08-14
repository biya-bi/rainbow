/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.core.entities.Department;
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
public class DepartmentLazyDataModel extends IntegerIdTrackableLazyDataModel<Department> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1411352885652093880L;

    private static final String NAME_FILTER = "name";

    private final List<Filter<?>> filters;

    private final SingleValuedFilter<String> nameFilter;

	@Autowired
	@Qualifier("departmentService")
	private Service<Department, Integer, SearchOptions> service;
	
    public DepartmentLazyDataModel() {
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
    protected void sort(String sortField, SortOrder sortOrder, List<Department> list) {
        super.sort(sortField, sortOrder, list);
        if (sortField == null) {
            sortField = NAME_FILTER; // We want to sort by name if no sort field was specified.
        }
        final SortOrder order = sortOrder;
        if (null != sortField) {
            switch (sortField) {
                case NAME_FILTER: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<Department>() {
                        @Override
                        public int compare(Department one, Department other) {
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
	protected Service<Department, Integer, SearchOptions> getService() {
		return service;
	}

}
