package org.rainbow.asset.explorer.faces.controllers.read;

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
public class AssetDocumentReadController extends AbstractNumericIdAuditableEntityReadController<AssetDocument, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7114484409233265906L;

	private final EnumTranslator translator;

	private static final String FILE_NAME_PATH = "fileName";
	private static final String DOCUMENT_TYPE_PATH = "documentType";
	private static final String SITE_LOCATION_PATH = "asset.location";
	private static final String SITE_NAME_PATH = "asset.name";
	private static final String SITE_STATUS_PATH = "asset.status";

	private final StringSearchCriterion fileNameSearchCriterion;
	private final StringSearchCriterion documentTypeSearchCriterion;
	private final StringSearchCriterion assetLocationSearchCriterion;
	private final StringSearchCriterion assetNameSearchCriterion;
	private final StringSearchCriterion assetStateSearchCriterion;

	@Autowired
	private AssetDocumentService service;

	public AssetDocumentReadController() {
		super(Long.class);
		translator = new EnumTranslator();

		fileNameSearchCriterion = new StringSearchCriterion(FILE_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		documentTypeSearchCriterion = new StringSearchCriterion(DOCUMENT_TYPE_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		assetLocationSearchCriterion = new StringSearchCriterion(SITE_LOCATION_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		assetNameSearchCriterion = new StringSearchCriterion(SITE_NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		assetStateSearchCriterion = new StringSearchCriterion(SITE_STATUS_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<AssetDocument> list) {
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
			case DOCUMENT_TYPE_PATH: {
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
			// case SITE_NAME_PATH: {
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
			// case SITE_LOCATION_PATH: {
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
			// case SITE_STATUS_PATH: {
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

	@SearchCriterion
	public StringSearchCriterion getFileNameSearchCriterion() {
		return fileNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getDocumentTypeSearchCriterion() {
		return documentTypeSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getAssetLocationSearchCriterion() {
		return assetLocationSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getAssetNameSearchCriterion() {
		return assetNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getAssetStateSearchCriterion() {
		return assetStateSearchCriterion;
	}

	@Override
	public Service<AssetDocument> getService() {
		return service;
	}

}
