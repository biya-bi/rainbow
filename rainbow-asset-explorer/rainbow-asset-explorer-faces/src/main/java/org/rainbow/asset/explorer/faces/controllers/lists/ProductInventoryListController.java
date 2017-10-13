package org.rainbow.asset.explorer.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.service.services.ProductInventoryService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.lists.AbstractAuditableListController;
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
public class ProductInventoryListController extends AbstractAuditableListController<ProductInventory> {

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

	public ProductInventoryListController() {
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
	protected void sort(String sortField, SortOrder sortOrder, List<ProductInventory> list) {
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

	@Override
	protected Service<ProductInventory> getService() {
		return productInventoryService;
	}

	@Override
	protected Object convert(String rowKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
