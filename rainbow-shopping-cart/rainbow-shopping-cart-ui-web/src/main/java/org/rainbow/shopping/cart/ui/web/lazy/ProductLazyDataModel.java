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

import javax.faces.bean.RequestScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.service.Service;
import org.rainbow.shopping.cart.core.entities.Product;
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
public class ProductLazyDataModel extends LongIdTrackableLazyDataModel<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 239573269282240813L;

	@Autowired
	@Qualifier("productService")
	private Service<Product, Long, SearchOptions> service;

	private static final String NAME_FILTER = "name";
	private static final String CODE_FILTER = "code";
	private static final String PRICE_FILTER = "price";
	private static final String CATEGORY_NAME_FILTER = "category.name";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> codeFilter;
	private final SingleValuedFilter<String> priceFilter;
	private final SingleValuedFilter<String> categoryNameFilter;

	public ProductLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		codeFilter = new SingleValuedFilter<>(CODE_FILTER, RelationalOperator.CONTAINS, "");
		priceFilter = new SingleValuedFilter<>(PRICE_FILTER);
		categoryNameFilter = new SingleValuedFilter<>(CATEGORY_NAME_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(codeFilter);
		filters.add(priceFilter);
		filters.add(categoryNameFilter);
	}

	@Override
	protected Service<Product, Long, SearchOptions> getService() {
		return service;
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getCodeFilter() {
		return codeFilter;
	}

	public SingleValuedFilter<String> getPriceFilter() {
		return priceFilter;
	}

	public SingleValuedFilter<String> getCategoryNameFilter() {
		return categoryNameFilter;
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
	protected void sort(String sortField, SortOrder sortOrder, List<Product> list) {
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
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case CODE_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						int result = comparator.compare(one.getCode(), other.getCode());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case PRICE_FILTER: {
				final Comparator<Double> comparator = DefaultComparator.<Double>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						int result = comparator.compare(one.getPrice(), other.getPrice());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case CATEGORY_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						if (one.getCategory() == null && other.getCategory() == null) {
							return 0;
						}
						if (one.getCategory() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getCategory() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getCategory().getName(), other.getCategory().getName());
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
