package org.rainbow.security.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion.Operator;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.service.services.ApplicationService;
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
public class ApplicationReadController extends AbstractNumericIdAuditableEntityReadController<Application, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5957873059508715867L;

	private static final String NAME_PATH = "name";

	private final StringSearchCriterion nameSearchCriterion;

	@Autowired
	private ApplicationService applicationService;

	public ApplicationReadController() {
		super(Long.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Application> list) {
		if (sortField == null) {
			sortField = NAME_PATH; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Application>() {
					@Override
					public int compare(Application one, Application other) {
						int result = comparator.compare(one.getName(), other.getName());
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
	public Service<Application> getService() {
		return applicationService;
	}

}
