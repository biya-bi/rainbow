package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.SiteDocument;
import org.rainbow.asset.explorer.service.services.SiteDocumentService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.read.AbstractNumericIdAuditableEntityReadController;
import org.rainbow.faces.util.SearchCriterion;
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
public class SiteDocumentReadController extends AbstractNumericIdAuditableEntityReadController<SiteDocument, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3978923460418331919L;

	private final EnumTranslator translator;

	private static final String FILE_NAME_PATH = "fileName";
	private static final String DOCUMENT_TYPE_PATH = "documentType";
	private static final String SITE_LOCATION_PATH = "site.location";
	private static final String SITE_NAME_PATH = "site.name";
	private static final String SITE_STATUS_PATH = "site.status";

	private final StringSearchCriterion fileNameSearchCriterion;
	private final StringSearchCriterion documentTypeSearchCriterion;
	private final StringSearchCriterion siteLocationSearchCriterion;
	private final StringSearchCriterion siteNameSearchCriterion;
	private final StringSearchCriterion siteStatusSearchCriterion;

	@Autowired
	private SiteDocumentService service;

	public SiteDocumentReadController() {
		super(Long.class);
		translator = new EnumTranslator();

		fileNameSearchCriterion = new StringSearchCriterion(FILE_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		documentTypeSearchCriterion = new StringSearchCriterion(DOCUMENT_TYPE_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		siteLocationSearchCriterion = new StringSearchCriterion(SITE_LOCATION_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		siteNameSearchCriterion = new StringSearchCriterion(SITE_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		siteStatusSearchCriterion = new StringSearchCriterion(SITE_STATUS_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<SiteDocument> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = FILE_NAME_PATH; // We want to sort by name if no sort
											// field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case FILE_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<SiteDocument>() {
					@Override
					public int compare(SiteDocument one, SiteDocument other) {
						int result = comparator.compare(one.getFileName(), other.getFileName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case DOCUMENT_TYPE_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<SiteDocument>() {
					@Override
					public int compare(SiteDocument one, SiteDocument other) {
						int result = comparator.compare(translator.translate(one.getDocumentType()),
								translator.translate(other.getDocumentType()));
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SITE_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<SiteDocument>() {
					@Override
					public int compare(SiteDocument one, SiteDocument other) {
						int result = comparator.compare(one.getSite().getName(), other.getSite().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SITE_LOCATION_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<SiteDocument>() {
					@Override
					public int compare(SiteDocument one, SiteDocument other) {
						int result = comparator.compare(one.getSite().getLocation(), other.getSite().getLocation());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SITE_STATUS_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<SiteDocument>() {
					@Override
					public int compare(SiteDocument one, SiteDocument other) {
						int result = comparator.compare(translator.translate(one.getSite().getStatus()),
								translator.translate(other.getSite().getStatus()));
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

	@SearchCriterion
	public StringSearchCriterion getFileNameSearchCriterion() {
		return fileNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getDocumentTypeSearchCriterion() {
		return documentTypeSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSiteLocationSearchCriterion() {
		return siteLocationSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSiteNameSearchCriterion() {
		return siteNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSiteStatusSearchCriterion() {
		return siteStatusSearchCriterion;
	}

	@Override
	public Service<SiteDocument> getService() {
		return service;
	}

}
