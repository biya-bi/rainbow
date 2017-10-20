package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.Alert;
import org.rainbow.asset.explorer.service.services.AlertService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
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
public class AlertReadController extends AbstractNumericIdAuditableEntityReadController<Alert, Integer> {

	/**
	 *
	 */
	private static final long serialVersionUID = 6995192585565622256L;

	private final EnumTranslator translator;

	private static final String ALERT_TYPE_FILTER = "alertType";
	private static final String ALERT_CATEGORY_FILTER = "alertCategory";
	private static final String ENABLED_FILTER = "enabled";
	private static final String IMMEDIATE_FILTER = "immediate";

	private final SingleValuedFilter<String> alertTypeFilter;
	private final SingleValuedFilter<String> alertCategoryFilter;
	private final SingleValuedFilter<Boolean> enabledFilter;
	private final SingleValuedFilter<Boolean> immediateFilter;

	@Autowired
	private AlertService service;

	public AlertReadController() {
		super(Integer.class);
		translator = new EnumTranslator();

		alertTypeFilter = new SingleValuedFilter<>(ALERT_TYPE_FILTER, RelationalOperator.CONTAINS, "");
		alertCategoryFilter = new SingleValuedFilter<>(ALERT_CATEGORY_FILTER, RelationalOperator.CONTAINS, "");
		enabledFilter = new SingleValuedFilter<>(ENABLED_FILTER, RelationalOperator.EQUAL, null);
		immediateFilter = new SingleValuedFilter<>(IMMEDIATE_FILTER, RelationalOperator.EQUAL, null);
	}

	@Filterable
	public SingleValuedFilter<String> getAlertTypeFilter() {
		return alertTypeFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getAlertCategoryFilter() {
		return alertCategoryFilter;
	}

	@Filterable
	public SingleValuedFilter<Boolean> getEnabledFilter() {
		return enabledFilter;
	}

	@Filterable
	public SingleValuedFilter<Boolean> getImmediateFilter() {
		return immediateFilter;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Alert> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = ALERT_TYPE_FILTER; // We want to sort by name if no sort
			// field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case ALERT_TYPE_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Alert>() {
					@Override
					public int compare(Alert one, Alert other) {
						int result = comparator.compare(translator.translate(one.getAlertType()),
								translator.translate(other.getAlertType()));
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case ALERT_CATEGORY_FILTER: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Alert>() {
					@Override
					public int compare(Alert one, Alert other) {
						int result = comparator.compare(translator.translate(one.getAlertCategory()),
								translator.translate(other.getAlertCategory()));
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case ENABLED_FILTER: {
				final Comparator<Boolean> comparator = DefaultComparator.<Boolean>getInstance();
				Collections.sort(list, new Comparator<Alert>() {
					@Override
					public int compare(Alert one, Alert other) {
						int result = comparator.compare(one.isEnabled(), other.isEnabled());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case IMMEDIATE_FILTER: {
				final Comparator<Boolean> comparator = DefaultComparator.<Boolean>getInstance();
				Collections.sort(list, new Comparator<Alert>() {
					@Override
					public int compare(Alert one, Alert other) {
						int result = comparator.compare(one.isImmediate(), other.isImmediate());
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
	public Service<Alert> getService() {
		return service;
	}

}
