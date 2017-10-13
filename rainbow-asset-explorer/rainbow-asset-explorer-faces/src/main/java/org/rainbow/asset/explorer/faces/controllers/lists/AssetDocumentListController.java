package org.rainbow.asset.explorer.faces.controllers.lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.AssetDocument;
import org.rainbow.asset.explorer.service.services.AssetDocumentService;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.faces.controllers.lists.AbstractNumericIdAuditableListController;
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
public class AssetDocumentListController extends AbstractNumericIdAuditableListController<AssetDocument, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7114484409233265906L;

	private final EnumTranslator translator;

	private static final String FILE_NAME_FILTER = "fileName";
	private static final String DOCUMENT_TYPE_FILTER = "documentType";
	private static final String SITE_LOCATION_FILTER = "asset.location";
	private static final String SITE_NAME_FILTER = "asset.name";
	private static final String SITE_STATUS_FILTER = "asset.status";

	private final SingleValuedFilter<String> fileNameFilter;
	private final SingleValuedFilter<String> documentTypeFilter;
	private final SingleValuedFilter<String> assetLocationFilter;
	private final SingleValuedFilter<String> assetNameFilter;
	private final SingleValuedFilter<String> assetStateFilter;

	@Autowired
	private AssetDocumentService service;

	public AssetDocumentListController() {
		super(Long.class);
		translator = new EnumTranslator();

		fileNameFilter = new SingleValuedFilter<>(FILE_NAME_FILTER, RelationalOperator.CONTAINS, "");
		documentTypeFilter = new SingleValuedFilter<>(DOCUMENT_TYPE_FILTER, RelationalOperator.CONTAINS, "");
		assetLocationFilter = new SingleValuedFilter<>(SITE_LOCATION_FILTER, RelationalOperator.CONTAINS, "");
		assetNameFilter = new SingleValuedFilter<>(SITE_NAME_FILTER, RelationalOperator.CONTAINS, "");
		assetStateFilter = new SingleValuedFilter<>(SITE_STATUS_FILTER, RelationalOperator.CONTAINS, "");
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<AssetDocument> list) {
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
				Collections.sort(list, new Comparator<AssetDocument>() {
					@Override
					public int compare(AssetDocument one, AssetDocument other) {
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
				Collections.sort(list, new Comparator<AssetDocument>() {
					@Override
					public int compare(AssetDocument one, AssetDocument other) {
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
			// case SITE_NAME_FILTER: {
			// final Comparator<String> comparator =
			// DefaultComparator.<String>getInstance();
			// Collections.sort(list, new Comparator<AssetDocument>() {
			// @Override
			// public int compare(AssetDocument one, AssetDocument other) {
			// int result = comparator.compare(one.getAsset().getName(),
			// other.getAsset().getName());
			// if (order == SortOrder.DESCENDING) {
			// return -result;
			// }
			// return result;
			// }
			// });
			// break;
			// }
			// case SITE_LOCATION_FILTER: {
			// final Comparator<String> comparator =
			// DefaultComparator.<String>getInstance();
			// Collections.sort(list, new Comparator<AssetDocument>() {
			// @Override
			// public int compare(AssetDocument one, AssetDocument other) {
			// int result = comparator.compare(one.getAsset().getLocation(),
			// other.getAsset().getLocation());
			// if (order == SortOrder.DESCENDING) {
			// return -result;
			// }
			// return result;
			// }
			// });
			// break;
			// }
			// case SITE_STATUS_FILTER: {
			// final Comparator<String> comparator =
			// DefaultComparator.<String>getInstance();
			// Collections.sort(list, new Comparator<AssetDocument>() {
			// @Override
			// public int compare(AssetDocument one, AssetDocument other) {
			// int result =
			// comparator.compare(translator.translate(one.getAsset().getStatus()),
			// translator.translate(other.getAsset().getStatus()));
			// if (order == SortOrder.DESCENDING) {
			// return -result;
			// }
			// return result;
			// }
			// });
			// break;
			// }
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
	public SingleValuedFilter<String> getAssetLocationFilter() {
		return assetLocationFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getAssetNameFilter() {
		return assetNameFilter;
	}

	@Filterable
	public SingleValuedFilter<String> getAssetStateFilter() {
		return assetStateFilter;
	}

	@Override
	protected Service<AssetDocument> getService() {
		return service;
	}

}
