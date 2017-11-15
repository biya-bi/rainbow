package org.rainbow.faces.controllers.read;

import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rainbow.common.util.BeanUtil;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.faces.controllers.ServiceController;
import org.rainbow.faces.util.SearchCriteriaUtil;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Biya-Bi
 * @param <T>
 */
public abstract class AbstractReadController<T> extends LazyDataModel<T> implements ServiceController<T> {

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

	public AbstractReadController() {
	}

	protected abstract Object convert(String rowKey);

	@Override
	public T getRowData(String rowKey) {
		try {
			return getService().findById(convert(rowKey));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object getRowKey(T object) {
		return BeanUtil.getProperty(object, "id");
	}

	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {

		int pageIndex = pageSize == 0 ? 0 : first / pageSize;

		Service<T> service = getService();
		List<T> result;
		try {
			SearchOptions searchOptions = searchOptionsFactory.create(pageIndex, pageSize,
					SearchCriteriaUtil.getPredicate(this, predicateBuilderFactory, pathFactory));
			result = service.find(searchOptions);
			setRowCount((int) service.count(searchOptions.getPredicate()));

			sort(sortField, sortOrder, result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return result;
	}

	protected void sort(String sortField, SortOrder sortOrder, List<T> list) {
	}
}
