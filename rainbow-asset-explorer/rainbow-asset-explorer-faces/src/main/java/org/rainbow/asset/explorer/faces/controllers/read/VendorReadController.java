package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.Vendor;
import org.rainbow.asset.explorer.service.services.VendorService;
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
public class VendorReadController extends AbstractNumericIdAuditableEntityReadController<Vendor, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7448646464760524071L;

	private static final String NAME_PATH = "name";
	private static final String ACCOUNT_NUMBER_PATH = "accountNumber";
	private static final String PURCHASING_URL_PATH = "purchasingUrl";
	private static final String ACTIVE_PATH = "active";

	private final StringSearchCriterion nameSearchCriterion;
	private final StringSearchCriterion accountNumberSearchCriterion;
	private final StringSearchCriterion purchasingUrlSearchCriterion;
	private final ComparableSearchCriterion<Boolean> activeSearchCriterion;

	@Autowired
	private VendorService service;

	public VendorReadController() {
		super(Long.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		accountNumberSearchCriterion = new StringSearchCriterion(ACCOUNT_NUMBER_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		purchasingUrlSearchCriterion = new StringSearchCriterion(PURCHASING_URL_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		activeSearchCriterion = new ComparableSearchCriterion<>(ACTIVE_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getAccountNumberSearchCriterion() {
		return accountNumberSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getPurchasingUrlSearchCriterion() {
		return purchasingUrlSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Boolean> getActiveSearchCriterion() {
		return activeSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Vendor> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = NAME_PATH; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Vendor>() {
					@Override
					public int compare(Vendor one, Vendor other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case ACCOUNT_NUMBER_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Vendor>() {
					@Override
					public int compare(Vendor one, Vendor other) {
						int result = comparator.compare(one.getAccountNumber(), other.getAccountNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case PURCHASING_URL_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Vendor>() {
					@Override
					public int compare(Vendor one, Vendor other) {
						int result = comparator.compare(one.getPurchasingUrl(), other.getPurchasingUrl());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case ACTIVE_PATH: {
				final Comparator<Boolean> comparator = DefaultComparator.<Boolean>getInstance();
				Collections.sort(list, new Comparator<Vendor>() {
					@Override
					public int compare(Vendor one, Vendor other) {
						int result = comparator.compare(one.isActive(), other.isActive());
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
	public Service<Vendor> getService() {
		return service;
	}

}
