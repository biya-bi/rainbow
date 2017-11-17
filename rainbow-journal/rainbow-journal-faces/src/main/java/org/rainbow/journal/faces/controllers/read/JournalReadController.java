package org.rainbow.journal.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.service.services.JournalService;
import org.rainbow.search.criteria.ComparableSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Named
@ViewScoped
public class JournalReadController extends AbstractNumericIdAuditableEntityReadController<Journal, Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6796618236115317151L;

	@Autowired
	@Qualifier("journalService")
	private JournalService service;

	private static final String NAME_PATH = "name";
	private static final String OWNER_PROFILE_USER_NAME_PATH = "ownerProfile.userName";
	private static final String ACTIVE_PATH = "active";
	private static final String TAG_PATH = "tag";

	private final StringSearchCriterion nameSearchCriterion;
	private final StringSearchCriterion ownerProfileUserNameSearchCriterion;
	private final ComparableSearchCriterion<Boolean> activeSearchCriterion;
	private final StringSearchCriterion tagSearchCriterion;

	public JournalReadController() {
		super(Long.class);

		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		ownerProfileUserNameSearchCriterion = new StringSearchCriterion(OWNER_PROFILE_USER_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		activeSearchCriterion = new ComparableSearchCriterion<>(ACTIVE_PATH, ComparableSearchCriterion.Operator.EQUAL,
				null);
		tagSearchCriterion = new StringSearchCriterion(TAG_PATH, StringSearchCriterion.Operator.CONTAINS, null);

	}

	@Override
	public Service<Journal> getService() {
		return service;
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getOwnerProfileUserNameSearchCriterion() {
		return ownerProfileUserNameSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Boolean> getActiveSearchCriterion() {
		return activeSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getTagSearchCriterion() {
		return tagSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Journal> list) {
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
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case OWNER_PROFILE_USER_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.getOwnerProfile().getUserName(),
								other.getOwnerProfile().getUserName());
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
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.isActive(), other.isActive());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case TAG_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Journal>() {
					@Override
					public int compare(Journal one, Journal other) {
						int result = comparator.compare(one.getTag(), other.getTag());
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

}
