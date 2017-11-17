package org.rainbow.journal.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.service.services.PublicationService;
import org.rainbow.search.criteria.ComparableSearchCriterion;
import org.rainbow.search.criteria.StringSearchCriterion;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Named
@ViewScoped
public class PublicationReadController extends AbstractNumericIdAuditableEntityReadController<Publication, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3822591215007082723L;

	@Autowired
	@Qualifier("publicationService")
	private PublicationService service;

	private static final String PUBLICATION_DATE_PATH = "publicationDate";
	private static final String JOURNAL_NAME_PATH = "journal.name";
	private static final String PUBLISHER_USER_NAME_PATH = "publisherProfile.userName";

	private final ComparableSearchCriterion<Date> publicationDateSearchCriterion;
	private final StringSearchCriterion journalNameSearchCriterion;
	private final StringSearchCriterion publisherUserNameSearchCriterion;

	public PublicationReadController() {
		super(Long.class);

		publicationDateSearchCriterion = new ComparableSearchCriterion<>(PUBLICATION_DATE_PATH,
				ComparableSearchCriterion.Operator.EQUAL, null);
		journalNameSearchCriterion = new StringSearchCriterion(JOURNAL_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		publisherUserNameSearchCriterion = new StringSearchCriterion(PUBLISHER_USER_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
	}

	@Override
	public Service<Publication> getService() {
		return service;
	}

	@SearchCriterion
	public StringSearchCriterion getJournalNameSearchCriterion() {
		return journalNameSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getPublicationDateSearchCriterion() {
		return publicationDateSearchCriterion;
	}

	public StringSearchCriterion getPublisherUserNameSearchCriterion() {
		return publisherUserNameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Publication> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = JOURNAL_NAME_PATH;
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case PUBLICATION_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<Publication>() {
					@Override
					public int compare(Publication one, Publication other) {
						int result = comparator.compare(one.getPublicationDate(), other.getPublicationDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case JOURNAL_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Publication>() {
					@Override
					public int compare(Publication one, Publication other) {
						if (one.getJournal() == null && other.getJournal() == null) {
							return 0;
						}
						if (one.getJournal() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getJournal() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getJournal().getName(), other.getJournal().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case PUBLISHER_USER_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Publication>() {
					@Override
					public int compare(Publication one, Publication other) {
						int result = comparator.compare(one.getPublisherProfile().getUserName(),
								other.getPublisherProfile().getUserName());
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