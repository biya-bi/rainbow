package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Location;
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.persistence.dao.InventoryManager;
import org.rainbow.common.util.DefaultComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class InventoryLazyDataModel extends LazyDataModel<Map.Entry<Product, Short>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3493803572871669696L;

	private static final String NAME_FILTER = "key.name";
	private static final String NUMBER_FILTER = "key.number";
	private static final String QUANTITY_FILTER = "value";

	private Location location;
	private String number;
	private String name;

	@Autowired
	private InventoryManager inventoryManager;

	public InventoryLazyDataModel() {
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<Map.Entry<Product, Short>> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		int pageIndex = pageSize == 0 ? 0 : first / pageSize;
		long locationId = location.getId();

		Map<Product, Short> inventory;
		try {
			inventory = inventoryManager.load(locationId, number, name, pageIndex, pageSize);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		long count = inventoryManager.count(locationId, number, name);
		Set<Map.Entry<Product, Short>> productSet = inventory.entrySet();

		ArrayList<Map.Entry<Product, Short>> result = new ArrayList<>(productSet);
		setRowCount((int) count);

		sort(sortField, sortOrder, result);

		return result;

	}

	protected void sort(String sortField, SortOrder sortOrder, List<Map.Entry<Product, Short>> list) {
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Map.Entry<Product, Short>>() {
					@Override
					public int compare(Map.Entry<Product, Short> one, Map.Entry<Product, Short> other) {
						int result = comparator.compare(one.getKey().getName(), other.getKey().getName());
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
				Collections.sort(list, new Comparator<Map.Entry<Product, Short>>() {
					@Override
					public int compare(Map.Entry<Product, Short> one, Map.Entry<Product, Short> other) {
						int result = comparator.compare(one.getKey().getNumber(), other.getKey().getNumber());
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
				Collections.sort(list, new Comparator<Map.Entry<Product, Short>>() {
					@Override
					public int compare(Map.Entry<Product, Short> one, Map.Entry<Product, Short> other) {
						int result = comparator.compare(one.getValue(), other.getValue());
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
