package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.service.services.ProductService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
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
public class ProductReadController extends AbstractNumericIdAuditableEntityReadController<Product, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7519254045099132912L;

	private static final String NAME_PATH = "name";
	private static final String NUMBER_PATH = "number";
	private static final String SAFETY_STOCK_LEVEL_PATH = "safetyStockLevel";
	private static final String STOCK_COVER_PATH = "stockCover";
	private static final String REORDER_POINT_PATH = "reorderPoint";
	private static final String MANUFACTURER_NAME_PATH = "manufacturer.name";

	private final StringSearchCriterion nameSearchCriterion;
	private final StringSearchCriterion numberSearchCriterion;
	private final ComparableSearchCriterion<Short> safetyStockLevelSearchCriterion;
	private final ComparableSearchCriterion<Short> stockCoverSearchCriterion;
	private final ComparableSearchCriterion<Short> reorderPointSearchCriterion;
	private final StringSearchCriterion manufacturerNameSearchCriterion;

	@Autowired
	private ProductService service;

	public ProductReadController() {
		super(Long.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		numberSearchCriterion = new StringSearchCriterion(NUMBER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		safetyStockLevelSearchCriterion = new ComparableSearchCriterion<>(SAFETY_STOCK_LEVEL_PATH);
		stockCoverSearchCriterion = new ComparableSearchCriterion<>(STOCK_COVER_PATH);
		reorderPointSearchCriterion = new ComparableSearchCriterion<>(REORDER_POINT_PATH);
		manufacturerNameSearchCriterion = new StringSearchCriterion(MANUFACTURER_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getNumberSearchCriterion() {
		return numberSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Short> getSafetyStockLevelSearchCriterion() {
		return safetyStockLevelSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Short> getStockCoverSearchCriterion() {
		return stockCoverSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Short> getReorderPointSearchCriterion() {
		return reorderPointSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getManufacturerNameSearchCriterion() {
		return manufacturerNameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Product> list) {
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
			case NUMBER_PATH: {
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
			case SAFETY_STOCK_LEVEL_PATH: {
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
			case STOCK_COVER_PATH: {
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
			case REORDER_POINT_PATH: {
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
			case MANUFACTURER_NAME_PATH: {
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
	public Service<Product> getService() {
		return service;
	}

}
