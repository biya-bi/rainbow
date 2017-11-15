package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.ProductIssue;
import org.rainbow.asset.explorer.service.services.ProductIssueService;
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
public class ProductIssueReadController extends AbstractNumericIdAuditableEntityReadController<ProductIssue, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5288934312401804012L;

	private static final String REFERENCE_NUMBER_PATH = "referenceNumber";
	private static final String LOCATION_NAME_PATH = "location.name";
	private static final String REQUISITIONER_PATH = "requisitioner";
	private static final String ISSUE_DATE_PATH = "issueDate";
	private static final String DEPARTMENT_NAME_PATH = "department.name";

	private final StringSearchCriterion referenceNumberSearchCriterion;
	private final StringSearchCriterion locationNameSearchCriterion;
	private final StringSearchCriterion requisitionerSearchCriterion;
	private final ComparableSearchCriterion<Date> issueDateSearchCriterion;
	private final StringSearchCriterion departmentNameSearchCriterion;

	@Autowired
	private ProductIssueService service;

	public ProductIssueReadController() {
		super(Long.class);
		referenceNumberSearchCriterion = new StringSearchCriterion(REFERENCE_NUMBER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		locationNameSearchCriterion = new StringSearchCriterion(LOCATION_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		requisitionerSearchCriterion = new StringSearchCriterion(REQUISITIONER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		issueDateSearchCriterion = new ComparableSearchCriterion<>(ISSUE_DATE_PATH, ComparableSearchCriterion.Operator.EQUAL, new Date());
		departmentNameSearchCriterion = new StringSearchCriterion(DEPARTMENT_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getReferenceNumberSearchCriterion() {
		return referenceNumberSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getLocationNameSearchCriterion() {
		return locationNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getRequisitionerSearchCriterion() {
		return requisitionerSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getIssueDateSearchCriterion() {
		return issueDateSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getDepartmentNameSearchCriterion() {
		return departmentNameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<ProductIssue> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = REFERENCE_NUMBER_PATH; // We want to sort by reference
													// number if no sort field
													// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case REFERENCE_NUMBER_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductIssue>() {
					@Override
					public int compare(ProductIssue one, ProductIssue other) {
						int result = comparator.compare(one.getReferenceNumber(), other.getReferenceNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LOCATION_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductIssue>() {
					@Override
					public int compare(ProductIssue one, ProductIssue other) {
						int result = comparator.compare(one.getLocation().getName(), other.getLocation().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case REQUISITIONER_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductIssue>() {
					@Override
					public int compare(ProductIssue one, ProductIssue other) {
						int result = comparator.compare(one.getRequisitioner(), other.getRequisitioner());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case DEPARTMENT_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<ProductIssue>() {
					@Override
					public int compare(ProductIssue one, ProductIssue other) {
						if (one.getDepartment() == null && other.getDepartment() == null) {
							return 0;
						}
						if (one.getDepartment() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getDepartment() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getDepartment().getName(), other.getDepartment().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case ISSUE_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<ProductIssue>() {
					@Override
					public int compare(ProductIssue one, ProductIssue other) {
						int result = comparator.compare(one.getIssueDate(), other.getIssueDate());
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
	public Service<ProductIssue> getService() {
		return service;
	}

}
