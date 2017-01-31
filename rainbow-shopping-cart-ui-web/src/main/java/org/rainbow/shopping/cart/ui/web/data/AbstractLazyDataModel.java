/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.data;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rainbow.persistence.dao.Filter;
import org.rainbow.persistence.dao.SearchOptions;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.ui.web.utilities.BeanUtilities;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 * @param <TId>
 */
public abstract class AbstractLazyDataModel<TModel, TId> extends LazyDataModel<TModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8283281893367824155L;
	private final SearchOptions options;

	public AbstractLazyDataModel() {
		options = new SearchOptions();
	}

	protected abstract IService<TModel> getService();

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

		IService<TModel> service = getService();
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
