/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.lazy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.persistence.dao.Filter;
import org.rainbow.persistence.dao.RelationalOperator;
import org.rainbow.persistence.dao.SingleValuedFilter;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.model.Category;
import org.rainbow.shopping.cart.ui.web.utilities.DefaultComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@RequestScoped
public class CategoryLazyDataModel extends LongIdTrackableLazyDataModel<Category> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6796618236115317151L;

	@Autowired
	@Qualifier("categoryService")
	private IService<Category> service;

	private static final String NAME_FILTER = "name";
	private static final String PARENT_NAME_FILTER = "parent.name";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;

	private final SingleValuedFilter<String> parentNameFilter;

	public CategoryLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		parentNameFilter = new SingleValuedFilter<>(PARENT_NAME_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(parentNameFilter);
	}

	@Override
	protected IService<Category> getService() {
		return service;
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getParentNameFilter() {
		return parentNameFilter;
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
	protected void sort(String sortField, SortOrder sortOrder, List<Category> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Category>() {
					@Override
					public int compare(Category one, Category other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case PARENT_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Category>() {
					@Override
					public int compare(Category one, Category other) {
						if (one.getParent() == null && other.getParent() == null) {
							return 0;
						}
						if (one.getParent() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getParent() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getParent().getName(), other.getParent().getName());
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
}
