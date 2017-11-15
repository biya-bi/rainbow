/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.lazy;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.rainbow.shopping.cart.ui.web.utilities.BeanUtilities;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 * @param <TKey>
 */
public abstract class AbstractLazyDataModel<TEntity, TKey extends Serializable> extends LazyDataModel<TEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8283281893367824155L;
	private final SearchOptions options;

	public AbstractLazyDataModel() {
		options = new SearchOptions();
	}

	protected abstract Service<TEntity, TKey, SearchOptions> getService();

	protected abstract TKey toModelId(String rowKey);

	@Override
	public TEntity getRowData(String rowKey) {
		try {
			return getService().findById(toModelId(rowKey));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getRowKey(TEntity object) {
		return BeanUtilities.getProperty(object, "id");
	}

	protected List<Filter<?>> getFilters() {
		return null;
	}

	@Override
	public List<TEntity> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		int pageIndex = pageSize == 0 ? 0 : first / pageSize;

		options.setPageIndex(pageIndex);
		options.setPageSize(pageSize);
		options.setFilters(getFilters());

		Service<TEntity, TKey, SearchOptions> service = getService();
		List<TEntity> result;
		try {
			result = service.find(options);
			setRowCount((int) service.count(options));

			sort(sortField, sortOrder, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	protected void sort(String sortField, SortOrder sortOrder, List<TEntity> list) {
	}
}
