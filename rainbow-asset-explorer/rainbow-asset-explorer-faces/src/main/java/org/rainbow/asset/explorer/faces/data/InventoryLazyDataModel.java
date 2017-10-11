package org.rainbow.asset.explorer.faces.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.service.services.ProductInventoryService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.faces.filters.RelationalOperator;
import org.rainbow.faces.filters.SingleValuedFilter;
import org.rainbow.faces.util.FilterUtil;
import org.rainbow.faces.util.Filterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class InventoryLazyDataModel extends LazyDataModel<ProductInventory> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493803572871669696L;

	private static final String LOCATION_ID_FILTER = "id.locationId";
	private static final String NAME_FILTER = "product.name";
	private static final String NUMBER_FILTER = "product.number";
	private static final String QUANTITY_FILTER = "quantity";

	private final SingleValuedFilter<Long> locationIdFilter;
	private final SingleValuedFilter<String> productNameFilter;
	private final SingleValuedFilter<String> productNumberFilter;
	private final SingleValuedFilter<Short> quantityFilter;

	@Autowired
	private ProductInventoryService productInventoryService;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	public InventoryLazyDataModel() {
		locationIdFilter = new SingleValuedFilter<>(LOCATION_ID_FILTER, RelationalOperator.EQUAL, null);
		productNameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		productNumberFilter = new SingleValuedFilter<>(NUMBER_FILTER, RelationalOperator.CONTAINS, "");
		quantityFilter = new SingleValuedFilter<>(QUANTITY_FILTER);
	}

	@Filterable
	public SingleValuedFilter<Long> getLocationIdFilter() {
		return locationIdFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getProductNameFilter() {
		return productNameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getProductNumberFilter() {
		return productNumberFilter;
	}

	@Filterable
	public SingleValuedFilter<Short> getQuantityFilter() {
		return quantityFilter;
	}

	@Override
	public List<ProductInventory> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		int pageIndex = pageSize == 0 ? 0 : first / pageSize;

		List<ProductInventory> result;

		try {
			SearchOptions searchOptions = searchOptionsFactory.create(pageIndex, pageSize,
					FilterUtil.getPredicate(this, predicateBuilderFactory, pathFactory));
			result = productInventoryService.find(searchOptions);
			setRowCount((int) productInventoryService.count(searchOptions.getPredicate()));

			sort(sortField, sortOrder, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;

	}

	protected void sort(String sortField, SortOrder sortOrder, List<ProductInventory> list) {
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductInventory>() {
					@Override
					public int compare(ProductInventory one, ProductInventory other) {
						int result = comparator.compare(one.getProduct().getName(), other.getProduct().getName());
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
				Collections.sort(list, new Comparator<ProductInventory>() {
					@Override
					public int compare(ProductInventory one, ProductInventory other) {
						int result = comparator.compare(one.getProduct().getNumber(), other.getProduct().getNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case QUANTITY_FILTER: {
				final Comparator<Short> comparator = DefaultComparator.<Short>getInstance();
				Collections.sort(list, new Comparator<ProductInventory>() {
					@Override
					public int compare(ProductInventory one, ProductInventory other) {
						int result = comparator.compare(one.getQuantity(), other.getQuantity());
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
