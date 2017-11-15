/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.ui.web.lazy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.primefaces.model.SortOrder;
import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.journal.ui.web.utilities.DefaultComparator;
import org.rainbow.orm.entities.Trackable;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 * @param <TKey>
 */
public abstract class TrackableLazyDataModel<TEntity extends Trackable<?>, TKey extends Serializable> extends AbstractLazyDataModel<TEntity, TKey> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2818461029343555835L;
	private static final String CREATOR_PATH = "creator";
    private static final String UPDATER_PATH = "updater";
    private static final String CREATION_DATE_PATH = "creationDate";
    private static final String LAST_UPDATE_DATE_PATH = "lastUpdateDate";

    private final List<Filter<?>> filters;

    private final StringSearchCriterion creatorSearchCriterion;
    private final StringSearchCriterion updaterSearchCriterion;
    private final SingleValuedFilter<Date> creationDateSearchCriterion;
    private final SingleValuedFilter<Date> lastUpdateDateSearchCriterion;

    public TrackableLazyDataModel() {

        creatorSearchCriterion = new SingleValuedFilter<>(CREATOR_PATH, StringOperator.CONTAINS, null);
        updaterSearchCriterion = new SingleValuedFilter<>(UPDATER_PATH, StringOperator.CONTAINS, null);
        creationDateSearchCriterion = new SingleValuedFilter<>(CREATION_DATE_PATH, RelationalOperator.EQUAL, null);
        lastUpdateDateSearchCriterion = new SingleValuedFilter<>(LAST_UPDATE_DATE_PATH, RelationalOperator.EQUAL, null);

        filters = new ArrayList<>();
        filters.add(creatorFilter);
        filters.add(updaterFilter);
        filters.add(creationDateFilter);
        filters.add(lastUpdateDateFilter);
    }

    @Override
    protected List<Filter<?>> getFilters() {
    	filters.remove(creationDateFilter);
		if (creationDateFilter.getValue() != null)
			filters.add(creationDateFilter);
		filters.remove(lastUpdateDateFilter);
		if (lastUpdateDateFilter.getValue() != null)
			filters.add(lastUpdateDateFilter);
        return filters;
    }

    public StringSearchCriterion getCreatorSearchCriterion() {
        return creatorSearchCriterion;
    }

    public StringSearchCriterion getUpdaterSearchCriterion() {
        return updaterSearchCriterion;
    }

    public SingleValuedFilter<Date> getCreationDateSearchCriterion() {
        return creationDateSearchCriterion;
    }

    public SingleValuedFilter<Date> getLastUpdateDateSearchCriterion() {
        return lastUpdateDateSearchCriterion;
    }

    @Override
    protected void sort(String sortField, SortOrder sortOrder, List<TEntity> list) {
        final SortOrder order = sortOrder;
        if (null != sortField) {
            switch (sortField) {
                case CREATOR_PATH: {
                    final Comparator<String> comparator = DefaultComparator.<String>getInstance();
                    Collections.sort(list, new Comparator<TEntity>() {
                        @Override
                        public int compare(TEntity one, TEntity other) {
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
                    Collections.sort(list, new Comparator<TEntity>() {
                        @Override
                        public int compare(TEntity one, TEntity other) {
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
                    Collections.sort(list, new Comparator<TEntity>() {
                        @Override
                        public int compare(TEntity one, TEntity other) {
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
                    Collections.sort(list, new Comparator<TEntity>() {
                        @Override
                        public int compare(TEntity one, TEntity other) {
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
