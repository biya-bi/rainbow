package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.Asset;
import org.rainbow.asset.explorer.service.services.AssetService;
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
public class AssetReadController extends AbstractNumericIdAuditableEntityReadController<Asset, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5000782598224274450L;

	private static final String NAME_PATH = "name";
	private static final String SERIAL_NUMBER_PATH = "serialNumber";
	private static final String MANUFACTURER_NAME_PATH = "product.manufacturer.name";
	private static final String ASSET_TYPE_NAME_PATH = "assetType.name";
	private static final String MANUFACTURER_BUSINESS_IMPACT_PATH = "manufacturerBusinessImpact";
	private static final String ASSET_STATE_PATH = "state";
	private static final String ACQUISITION_DATE_PATH = "acquisitionDate";
	private static final String EXPIRY_DATE_PATH = "expiryDate";
	private static final String WARRANTY_EXPIRY_DATE_PATH = "warrantyExpiryDate";
	private static final String DEPRECIATION_METHOD = "depreciationMethod";
	private static final String BARCODE_PATH = "barCode";
	private static final String PRODUCT_NAME_PATH = "product.name";
	private static final String SITE_LOCATION_PATH = "site.location";
	private static final String SITE_NAME_PATH = "site.name";
	private static final String SITE_STATUS_PATH = "site.status";
	private static final String VENDOR_NAME_PATH = "vendor.name";
	private static final String SITE_ID_PATH = "site.id";

	private final StringSearchCriterion nameSearchCriterion;
	private final StringSearchCriterion serialNumberSearchCriterion;
	private final StringSearchCriterion manufacturerNameSearchCriterion;
	private final StringSearchCriterion assetTypeSearchCriterion;
	private final StringSearchCriterion manufacturerBusinessImpactSearchCriterion;
	private final StringSearchCriterion stateSearchCriterion;
	private final ComparableSearchCriterion<Date> acquisitionDateSearchCriterion;
	private final ComparableSearchCriterion<Date> expiryDateSearchCriterion;
	private final ComparableSearchCriterion<Date> warrantyExpiryDateSearchCriterion;
	private final StringSearchCriterion depreciationMethodSearchCriterion;
	private final StringSearchCriterion barCodeSearchCriterion;
	private final StringSearchCriterion productNameSearchCriterion;
	private final StringSearchCriterion siteLocationSearchCriterion;
	private final StringSearchCriterion siteStatusSearchCriterion;
	private final StringSearchCriterion siteNameSearchCriterion;
	private final StringSearchCriterion vendorNameSearchCriterion;
	private final ComparableSearchCriterion<Long> siteIdSearchCriterion;

	private final EnumTranslator translator;

	@Autowired
	private AssetService service;

	public AssetReadController() {
		super(Long.class);
		translator = new EnumTranslator();

		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		serialNumberSearchCriterion = new StringSearchCriterion(SERIAL_NUMBER_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		manufacturerNameSearchCriterion = new StringSearchCriterion(MANUFACTURER_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		assetTypeSearchCriterion = new StringSearchCriterion(ASSET_TYPE_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		manufacturerBusinessImpactSearchCriterion = new StringSearchCriterion(MANUFACTURER_BUSINESS_IMPACT_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		stateSearchCriterion = new StringSearchCriterion(ASSET_STATE_PATH, StringSearchCriterion.Operator.CONTAINS,
				null);
		acquisitionDateSearchCriterion = new ComparableSearchCriterion<>(ACQUISITION_DATE_PATH,
				ComparableSearchCriterion.Operator.EQUAL, null);
		expiryDateSearchCriterion = new ComparableSearchCriterion<>(EXPIRY_DATE_PATH,
				ComparableSearchCriterion.Operator.EQUAL, null);
		warrantyExpiryDateSearchCriterion = new ComparableSearchCriterion<>(WARRANTY_EXPIRY_DATE_PATH,
				ComparableSearchCriterion.Operator.EQUAL, null);
		depreciationMethodSearchCriterion = new StringSearchCriterion(DEPRECIATION_METHOD,
				StringSearchCriterion.Operator.CONTAINS, null);
		barCodeSearchCriterion = new StringSearchCriterion(BARCODE_PATH, StringSearchCriterion.Operator.CONTAINS, null);
		productNameSearchCriterion = new StringSearchCriterion(PRODUCT_NAME_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		siteLocationSearchCriterion = new StringSearchCriterion(SITE_LOCATION_PATH,
				StringSearchCriterion.Operator.CONTAINS, null);
		siteStatusSearchCriterion = new StringSearchCriterion(SITE_STATUS_PATH, StringSearchCriterion.Operator.CONTAINS,
				null);
		siteNameSearchCriterion = new StringSearchCriterion(SITE_NAME_PATH, StringSearchCriterion.Operator.CONTAINS,
				null);
		vendorNameSearchCriterion = new StringSearchCriterion(VENDOR_NAME_PATH, StringSearchCriterion.Operator.CONTAINS,
				null);
		siteIdSearchCriterion = new ComparableSearchCriterion<>(SITE_ID_PATH);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSerialNumberSearchCriterion() {
		return serialNumberSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getManufacturerNameSearchCriterion() {
		return manufacturerNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getAssetTypeSearchCriterion() {
		return assetTypeSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getStateSearchCriterion() {
		return stateSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getDepreciationMethodSearchCriterion() {
		return depreciationMethodSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSiteLocationSearchCriterion() {
		return siteLocationSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getManufacturerBusinessImpactSearchCriterion() {
		return manufacturerBusinessImpactSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getAcquisitionDateSearchCriterion() {
		return acquisitionDateSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getExpiryDateSearchCriterion() {
		return expiryDateSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Date> getWarrantyExpiryDateSearchCriterion() {
		return warrantyExpiryDateSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getBarCodeSearchCriterion() {
		return barCodeSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getProductNameSearchCriterion() {
		return productNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSiteStatusSearchCriterion() {
		return siteStatusSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getSiteNameSearchCriterion() {
		return siteNameSearchCriterion;
	}

	@SearchCriterion
	public StringSearchCriterion getVendorNameSearchCriterion() {
		return vendorNameSearchCriterion;
	}

	@SearchCriterion
	public ComparableSearchCriterion<Long> getSiteIdSearchCriterion() {
		return siteIdSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Asset> list) {
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
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(one.getName(), other.getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SERIAL_NUMBER_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(one.getSerialNumber(), other.getSerialNumber());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case MANUFACTURER_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						if (one.getProduct() == null && other.getProduct() == null) {
							return 0;
						}
						if (one.getProduct() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getProduct() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						if (one.getProduct().getManufacturer() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getProduct().getManufacturer() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getProduct().getManufacturer().getName(),
								other.getProduct().getManufacturer().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}

			case ASSET_TYPE_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						if (one.getAssetType() == null && other.getAssetType() == null) {
							return 0;
						}
						if (one.getAssetType() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getAssetType() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getAssetType().getName(), other.getAssetType().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}

			case ASSET_STATE_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(translator.translate(one.getState()),
								translator.translate(other.getState()));
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case DEPRECIATION_METHOD: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(translator.translate(one.getDepreciationMethod()),
								translator.translate(other.getDepreciationMethod()));
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case MANUFACTURER_BUSINESS_IMPACT_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(translator.translate(one.getManufacturerBusinessImpact()),
								translator.translate(other.getManufacturerBusinessImpact()));
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case ACQUISITION_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(one.getAcquisitionDate(), other.getAcquisitionDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case EXPIRY_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(one.getExpiryDate(), other.getExpiryDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case WARRANTY_EXPIRY_DATE_PATH: {
				final Comparator<Date> comparator = DefaultComparator.<Date>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(one.getWarrantyExpiryDate(), other.getWarrantyExpiryDate());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case BARCODE_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(one.getBarCode(), other.getBarCode());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case PRODUCT_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						int result = comparator.compare(one.getProduct().getName(), other.getProduct().getName());
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
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						if (one.getSite() == null && other.getSite() == null) {
							return 0;
						}
						if (one.getSite() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getSite() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
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
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						if (one.getSite() == null && other.getSite() == null) {
							return 0;
						}
						if (one.getSite() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getSite() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
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
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						if (one.getSite() == null && other.getSite() == null) {
							return 0;
						}
						if (one.getSite() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getSite() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
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
			case VENDOR_NAME_PATH: {
				final Comparator<String> comparator = DefaultComparator.<String>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						if (one.getVendor() == null && other.getVendor() == null) {
							return 0;
						}
						if (one.getVendor() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getVendor() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getVendor().getName(), other.getVendor().getName());
						if (order == SortOrder.DESCENDING) {
							return -result;
						}
						return result;
					}
				});
				break;
			}
			case SITE_ID_PATH: {
				final Comparator<Long> comparator = DefaultComparator.<Long>getInstance();
				Collections.sort(list, new Comparator<Asset>() {
					@Override
					public int compare(Asset one, Asset other) {
						if (one.getSite() == null && other.getSite() == null) {
							return 0;
						}
						if (one.getSite() == null) {
							return order == SortOrder.DESCENDING ? 1 : -1;
						}
						if (other.getSite() == null) {
							return order == SortOrder.DESCENDING ? -1 : 1;
						}
						int result = comparator.compare(one.getSite().getId(), other.getSite().getId());
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
	public Service<Asset> getService() {
		return service;
	}

}
