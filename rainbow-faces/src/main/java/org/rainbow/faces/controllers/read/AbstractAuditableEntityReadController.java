package org.rainbow.faces.controllers.read;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.primefaces.event.ToggleEvent;
import org.primefaces.model.SortOrder;
import org.primefaces.model.Visibility;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.orm.entities.AbstractAuditableEntity;
import org.rainbow.search.criteria.ComparableSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion.Operator;

/**
 *
 * @author Biya-Bi
 * @param <T>
 */
public abstract class AbstractAuditableEntityReadController<T extends AbstractAuditableEntity<?>>
		extends AbstractReadController<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2818461029343555835L;
	private List<Boolean> auditColumnsStates;
	private static final String CREATOR_PATH = "creator";
	private static final String UPDATER_PATH = "updater";
	private static final String CREATION_DATE_PATH = "creationDate";
	private static final String LAST_UPDATE_DATE_PATH = "lastUpdateDate";

	private final StringSearchCriterion creatorSearchCriterion;
	private final StringSearchCriterion updaterSearchCriterion;
	private final ComparableSearchCriterion<Date> creationDateSearchCriterion;
	private final ComparableSearchCriterion<Date> lastUpdateDateSearchCriterion;

	public AbstractAuditableEntityReadController() {
		creatorSearchCriterion = new StringSearchCriterion(CREATOR_PATH, Operator.CONTAINS, null);
		updaterSearchCriterion = new StringSearchCriterion(UPDATER_PATH, Operator.CONTAINS, null);
		creationDateSearchCriterion = new ComparableSearchCriterion<>(CREATION_DATE_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
		lastUpdateDateSearchCriterion = new ComparableSearchCriterion<>(LAST_UPDATE_DATE_PATH, ComparableSearchCriterion.Operator.EQUAL, null);
	}

	@PostConstruct
	public void init() {
		auditColumnsStates = new ArrayList<>();
	}

	public List<Boolean> getAuditColumnsStates() {
		return auditColumnsStates;
	}

	public void onToggle(ToggleEvent e) {
		Integer index = (Integer) e.getData();
		int size = auditColumnsStates.size();
		if (auditColumnsStates.size() <= index) {
			for (int i = 0; i < index - size + 1; i++) {
				auditColumnsStates.add(false);
			}
		}
		auditColumnsStates.set(index, e.getVisibility() == Visibility.VISIBLE);
	}

	@SearchCriterion
	public StringSearchCriterion getCreatorSearchCriterion() {
		return creatorSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getUpdaterSearchCriterion() {
		return updaterSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getCreationDateSearchCriterion() {
		return creationDateSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getLastUpdateDateSearchCriterion() {
		return lastUpdateDateSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<T> list) {
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case CREATOR_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<T>() {
					@Override
					public int compare(T one, T other) {
						int result = comparator.compare(one.getCreator(), other.getCreator());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case UPDATER_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<T>() {
					@Override
					public int compare(T one, T other) {
						int result = comparator.compare(one.getUpdater(), other.getUpdater());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}

			case CREATION_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<T>() {
					@Override
					public int compare(T one, T other) {
						int result = comparator.compare(one.getCreationDate(), other.getCreationDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case LAST_UPDATE_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<T>() {
					@Override
					public int compare(T one, T other) {
						int result = comparator.compare(one.getLastUpdateDate(), other.getLastUpdateDate());
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
