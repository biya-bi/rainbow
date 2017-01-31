/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

import org.primefaces.model.SortOrder;
import org.rainbow.persistence.dao.Filter;
import org.rainbow.persistence.dao.RelationalOperator;
import org.rainbow.persistence.dao.SingleValuedFilter;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.model.Product;
import org.rainbow.shopping.cart.ui.web.utilities.DefaultComparator;

/**
 *
 * @author Biya-Bi
 */
// @Named
@ManagedBean(name = "productLazyDataModel")
@RequestScoped
public class ProductLazyDataModel extends LongIdTrackableLazyDataModel<Product> {

	public void setService(IService<Product> productService) {
		this.service = productService;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7519254045099132912L;

	@ManagedProperty(value = "#{productService}")
	private IService<Product> service;

	private static final String NAME_FILTER = "name";
	private static final String CODE_FILTER = "code";
	private static final String PRICE_FILTER = "price";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> codeFilter;
	private final SingleValuedFilter<String> priceFilter;

	public ProductLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		codeFilter = new SingleValuedFilter<>(CODE_FILTER, RelationalOperator.CONTAINS, "");
		priceFilter = new SingleValuedFilter<>(PRICE_FILTER);

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(codeFilter);
		filters.add(priceFilter);
	}

	@Override
	protected IService<Product> getService() {
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
			default:
				break;
			}
		}
	}

}
