package org.rainbow.asset.explorer.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.service.services.ProductService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.lists.AbstractNumericIdAuditableEntityListController;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.Filterable;
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
public class ProductListController extends AbstractNumericIdAuditableEntityListController<Product, Long> {

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

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> numberFilter;
	private final SingleValuedFilter<String> safetyStockLevelFilter;
	private final SingleValuedFilter<String> stockCoverFilter;
	private final SingleValuedFilter<String> reorderPointFilter;
	private final SingleValuedFilter<String> manufacturerNameFilter;

	@Autowired
	private ProductService service;

	public ProductListController() {
		super(Long.class);
		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		numberFilter = new SingleValuedFilter<>(NUMBER_FILTER, RelationalOperator.CONTAINS, "");
		safetyStockLevelFilter = new SingleValuedFilter<>(SAFETY_STOCK_LEVEL_FILTER);
		stockCoverFilter = new SingleValuedFilter<>(STOCK_COVER_FILTER);
		reorderPointFilter = new SingleValuedFilter<>(REORDER_POINT_FILTER);
		manufacturerNameFilter = new SingleValuedFilter<>(MANUFACTURER_NAME_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Filterable
	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getNumberFilter() {
		return numberFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getSafetyStockLevelFilter() {
		return safetyStockLevelFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getStockCoverFilter() {
		return stockCoverFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getReorderPointFilter() {
		return reorderPointFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getManufacturerNameFilter() {
		return manufacturerNameFilter;
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
	protected Service<Product> getService() {
		return service;
	}

}
