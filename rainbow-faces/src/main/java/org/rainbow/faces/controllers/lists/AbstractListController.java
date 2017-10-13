package org.rainbow.faces.controllers.lists;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rainbow.common.util.BeanUtil;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.faces.util.FilterUtil;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Biya-Bi
 * @param <TModel>
 * @param <TId>
 */
public abstract class AbstractListController<TModel> extends LazyDataModel<TModel> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8283281893367824155L;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	public AbstractListController() {
	}

	protected abstract Service<TModel> getService();

	protected abstract Object convert(String rowKey);

	@Override
	public TModel getRowData(String rowKey) {
		try {
			return getService().findById(convert(rowKey));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getRowKey(TModel object) {
		return BeanUtil.getProperty(object, "id");
	}

	@Override
	public List<TModel> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		int pageIndex = pageSize == 0 ? 0 : first / pageSize;

		Service<TModel> service = getService();
		List<TModel> result;
		try {
			SearchOptions searchOptions = searchOptionsFactory.create(pageIndex, pageSize,
					FilterUtil.getPredicate(this, predicateBuilderFactory, pathFactory));
			result = service.find(searchOptions);
			setRowCount((int) service.count(searchOptions.getPredicate()));

			sort(sortField, sortOrder, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	protected void sort(String sortField, SortOrder sortOrder, List<TModel> list) {
	}
}
