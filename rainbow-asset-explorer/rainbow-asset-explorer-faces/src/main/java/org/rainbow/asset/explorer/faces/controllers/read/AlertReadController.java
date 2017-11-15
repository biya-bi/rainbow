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
public class AlertReadController extends AbstractNumericIdAuditableEntityReadController<Alert, Integer> {

	/**
	 *
	 */
	private static final long serialVersionUID = 6995192585565622256L;

	private final EnumTranslator translator;

	private static final String ALERT_TYPE_PATH = "alertType";
	private static final String ALERT_CATEGORY_PATH = "alertCategory";
	private static final String ENABLED_PATH = "enabled";
	private static final String IMMEDIATE_PATH = "immediate";

	private final StringSearchCriterion alertTypeSearchCriterion;
	private final StringSearchCriterion alertCategorySearchCriterion;
	private final ComparableSearchCriterion<Boolean> enabledSearchCriterion;
	private final ComparableSearchCriterion<Boolean> immediateSearchCriterion;

	@Autowired
	private AlertService service;

	public AlertReadController() {
		super(Integer.class);
		translator = new EnumTranslator();

		alertTypeSearchCriterion = new StringSearchCriterion(ALERT_TYPE_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		alertCategorySearchCriterion = new StringSearchCriterion(ALERT_CATEGORY_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		enabledSearchCriterion = new ComparableSearchCriterion<>(ENABLED_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
		immediateSearchCriterion = new ComparableSearchCriterion<>(IMMEDIATE_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
	}

	@SearchCriterion
	public StringSearchCriterion getAlertTypeSearchCriterion() {
		return alertTypeSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getAlertCategorySearchCriterion() {
		return alertCategorySearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Boolean> getEnabledSearchCriterion() {
		return enabledSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Boolean> getImmediateSearchCriterion() {
		return immediateSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Alert> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = ALERT_TYPE_PATH; // We want to sort by name if no sort
			// field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case ALERT_TYPE_PATH: {
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
			case ALERT_CATEGORY_PATH: {
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
			case ENABLED_PATH: {
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
			case IMMEDIATE_PATH: {
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
