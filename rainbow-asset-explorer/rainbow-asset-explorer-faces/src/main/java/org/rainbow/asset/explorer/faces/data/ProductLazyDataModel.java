package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Product;
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
public class ProductLazyDataModel extends LongIdTrackableLazyDataModel<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7519254045099132912L;

	private static final String NAME_FILTER = "name";
	private static final String NUMBER_FILTER = "number";
	private static final String SAFETY_STOCK_LEVEL_FILTER = "safetyStockLevel";
	private static final String STOCK_COVER_FILTER = "stockCover";
	private static final String REORDER_POINT_FILTER = "reorderPoint";
	private static final String MANUFACTURER_NAME_FILTER = "manufacturer.name";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> numberFilter;
	private final SingleValuedFilter<String> safetyStockLevelFilter;
	private final SingleValuedFilter<String> stockCoverFilter;
	private final SingleValuedFilter<String> reorderPointFilter;
	private final SingleValuedFilter<String> manufacturerNameFilter;

	@Autowired
	@Qualifier("productService")
	private Service<Product, Long, SearchOptions> service;

	public ProductLazyDataModel() {
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		numberFilter = new SingleValuedFilter<>(NUMBER_FILTER, RelationalOperator.CONTAINS, "");
		safetyStockLevelFilter = new SingleValuedFilter<>(SAFETY_STOCK_LEVEL_FILTER);
		stockCoverFilter = new SingleValuedFilter<>(STOCK_COVER_FILTER);
		reorderPointFilter = new SingleValuedFilter<>(REORDER_POINT_FILTER);
		manufacturerNameFilter = new SingleValuedFilter<>(MANUFACTURER_NAME_FILTER, RelationalOperator.CONTAINS, "");

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(numberFilter);
		filters.add(safetyStockLevelFilter);
		filters.add(stockCoverFilter);
		filters.add(reorderPointFilter);
		filters.add(manufacturerNameFilter);
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getNumberFilter() {
		return numberFilter;
	}

	public SingleValuedFilter<String> getSafetyStockLevelFilter() {
		return safetyStockLevelFilter;
	}

	public SingleValuedFilter<String> getStockCoverFilter() {
		return stockCoverFilter;
	}

	public SingleValuedFilter<String> getReorderPointFilter() {
		return reorderPointFilter;
	}

	public SingleValuedFilter<String> getManufacturerNameFilter() {
		return manufacturerNameFilter;
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
			case NUMBER_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						int result = comparator.compare(one.getNumber(), other.getNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SAFETY_STOCK_LEVEL_FILTER: {
				final Comparator<Short> comparator = DefaultComparator.<Short>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						int result = comparator.compare(one.getSafetyStockLevel(), other.getSafetyStockLevel());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case STOCK_COVER_FILTER: {
				final Comparator<Short> comparator = DefaultComparator.<Short>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						int result = comparator.compare(one.getStockCover(), other.getStockCover());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case REORDER_POINT_FILTER: {
				final Comparator<Short> comparator = DefaultComparator.<Short>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						int result = comparator.compare(one.getReorderPoint(), other.getReorderPoint());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case MANUFACTURER_NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Product>() {
					@Override
					public int compare(Product one, Product other) {
						if (one.getManufacturer() == null && other.getManufacturer() == null) {
							return 0;
						}
						if (one.getManufacturer() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getManufacturer() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getManufacturer().getName(),
								other.getManufacturer().getName());
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
	protected Service<Product, Long, SearchOptions> getService() {
		return service;
	}

}
