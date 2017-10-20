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
public class SiteDocumentReadController extends AbstractNumericIdAuditableEntityReadController<SiteDocument, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3978923460418331919L;

	private final EnumTranslator translator;

	private static final String FILE_NAME_FILTER = "fileName";
	private static final String DOCUMENT_TYPE_FILTER = "documentType";
	private static final String SITE_LOCATION_FILTER = "site.location";
	private static final String SITE_NAME_FILTER = "site.name";
	private static final String SITE_STATUS_FILTER = "site.status";

	private final SingleValuedFilter<String> fileNameFilter;
	private final SingleValuedFilter<String> documentTypeFilter;
	private final SingleValuedFilter<String> siteLocationFilter;
	private final SingleValuedFilter<String> siteNameFilter;
	private final SingleValuedFilter<String> siteStatusFilter;

	@Autowired
	private SiteDocumentService service;

	public SiteDocumentReadController() {
		super(Long.class);
		translator = new EnumTranslator();

		fileNameFilter = new SingleValuedFilter<>(FILE_NAME_FILTER, RelationalOperator.CONTAINS, "");
		documentTypeFilter = new SingleValuedFilter<>(DOCUMENT_TYPE_FILTER, RelationalOperator.CONTAINS, "");
		siteLocationFilter = new SingleValuedFilter<>(SITE_LOCATION_FILTER, RelationalOperator.CONTAINS, "");
		siteNameFilter = new SingleValuedFilter<>(SITE_NAME_FILTER, RelationalOperator.CONTAINS, "");
		siteStatusFilter = new SingleValuedFilter<>(SITE_STATUS_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<SiteDocument> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = FILE_NAME_FILTER; // We want to sort by name if no sort
											// field was specified.
		}
		final SortOrder order = sortOrder;
		if (null != sortField) {
			switch (sortField) {
			case FILE_NAME_FILTER: {
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
			case DOCUMENT_TYPE_FILTER: {
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
			case SITE_NAME_FILTER: {
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
			case SITE_LOCATION_FILTER: {
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
			case SITE_STATUS_FILTER: {
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

	@Filterable
	public SingleValuedFilter<String> getFileNameFilter() {
		return fileNameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getDocumentTypeFilter() {
		return documentTypeFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getSiteLocationFilter() {
		return siteLocationFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getSiteNameFilter() {
		return siteNameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getSiteStatusFilter() {
		return siteStatusFilter;
	}

	@Override
	public Service<SiteDocument> getService() {
		return service;
	}

}
