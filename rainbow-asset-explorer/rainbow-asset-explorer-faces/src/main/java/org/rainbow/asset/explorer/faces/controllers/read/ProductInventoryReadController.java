package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.ProductInventory;
import org.rainbow.asset.explorer.service.services.ProductInventoryService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.search.criteria.ComparableSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;
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
public class ProductInventoryReadController extends AbstractAuditableEntityReadController<ProductInventory> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493803572871669696L;

	private static final String LOCATION_ID_PATH = "id.locationId";
	private static final String NAME_PATH = "product.name";
	private static final String NUMBER_PATH = "product.number";
	private static final String QUANTITY_PATH = "quantity";

	private final ComparableSearchCriterion<Long> locationIdSearchCriterion;
	private final StringSearchCriterion productNameSearchCriterion;
	private final StringSearchCriterion productNumberSearchCriterion;
	private final ComparableSearchCriterion<Short> quantitySearchCriterion;

	@Autowired
	private ProductInventoryService productInventoryService;

	public ProductInventoryReadController() {
		locationIdSearchCriterion = new ComparableSearchCriterion<>(LOCATION_ID_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
		productNameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		productNumberSearchCriterion = new StringSearchCriterion(NUMBER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		quantitySearchCriterion = new ComparableSearchCriterion<>(QUANTITY_PATH);
	}

	@SearchCriterion
	public ComparableSearchCriterion<Long> getLocationIdSearchCriterion() {
		return locationIdSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getProductNameSearchCriterion() {
		return productNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getProductNumberSearchCriterion() {
		return productNumberSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Short> getQuantitySearchCriterion() {
		return quantitySearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<ProductInventory> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = NAME_PATH; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_PATH: {
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
			case NUMBER_PATH: {
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
			case QUANTITY_PATH: {
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
	public Service<ProductInventory> getService() {
		return productInventoryService;
	}

	@Override
	protected Object convert(String rowKey) {
		// TODO Auto-generated method stub
		return null;
	}
}
