/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rainbow.common.util.BeanUtilities;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 * @param <TId>
 */
public abstract class AbstractLazyDataModel<TModel, TId extends Serializable> extends LazyDataModel<TModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8283281893367824155L;
	private final SearchOptions options;

	public AbstractLazyDataModel() {
		options = new SearchOptions();
	}

	protected abstract Service<TModel, TId, SearchOptions> getService();

	protected abstract TId toModelId(String rowKey);

	@Override
	public TModel getRowData(String rowKey) {
		try {
			return getService().findById(toModelId(rowKey));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getRowKey(TModel object) {
		return BeanUtilities.getProperty(object, "id");
	}

	protected List<Filter<?>> getFilters() {
		return null;
	}

	@Override
	public List<TModel> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		int pageIndex = pageSize == 0 ? 0 : first / pageSize;

		options.setPageIndex(pageIndex);
		options.setPageSize(pageSize);
		options.setFilters(getFilters());

		Service<TModel, TId, SearchOptions> service = getService();
		List<TModel> result;
		try {
			result = service.find(options);
			setRowCount((int) service.count(options));

			sort(sortField, sortOrder, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	protected void sort(String sortField, SortOrder sortOrder, List<TModel> list) {
	}
}
