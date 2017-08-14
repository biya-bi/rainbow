/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.core.entities.Asset;
import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.common.util.DefaultComparator;
import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class AssetLazyDataModel extends LongIdTrackableLazyDataModel<Asset> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5000782598224274450L;

	private static final String NAME_FILTER = "name";
	private static final String SERIAL_NUMBER_FILTER = "serialNumber";
	private static final String MANUFACTURER_NAME_FILTER = "product.manufacturer.name";
	private static final String ASSET_TYPE_NAME_FILTER = "assetType.name";
	private static final String MANUFACTURER_BUSINESS_IMPACT_FILTER = "manufacturerBusinessImpact";
	private static final String ASSET_STATE_FILTER = "state";
	private static final String ACQUISITION_DATE_FILTER = "acquisitionDate";
	private static final String EXPIRY_DATE_FILTER = "expiryDate";
	private static final String WARRANTY_EXPIRY_DATE_FILTER = "warrantyExpiryDate";
	private static final String DEPRECIATION_METHOD = "depreciationMethod";
	private static final String BARCODE_FILTER = "barCode";
	private static final String PRODUCT_NAME_FILTER = "product.name";
	private static final String SITE_LOCATION_FILTER = "site.location";
	private static final String SITE_NAME_FILTER = "site.name";
	private static final String SITE_STATUS_FILTER = "site.status";
	private static final String VENDOR_NAME_FILTER = "vendor.name";
	private static final String SITE_ID_FILTER = "site.id";

	private final List<Filter<?>> filters;

	private final SingleValuedFilter<String> nameFilter;
	private final SingleValuedFilter<String> serialNumberFilter;
	private final SingleValuedFilter<String> manufacturerNameFilter;
	private final SingleValuedFilter<String> assetTypeFilter;
	private final SingleValuedFilter<String> manufacturerBusinessImpactFilter;
	private final SingleValuedFilter<String> stateFilter;
	private final SingleValuedFilter<String> acquisitionDateFilter;
	private final SingleValuedFilter<String> expiryDateFilter;
	private final SingleValuedFilter<String> warrantyExpiryDateFilter;
	private final SingleValuedFilter<String> depreciationMethodFilter;
	private final SingleValuedFilter<String> barCodeFilter;
	private final SingleValuedFilter<String> productNameFilter;
	private final SingleValuedFilter<String> siteLocationFilter;
	private final SingleValuedFilter<String> siteStatusFilter;
	private final SingleValuedFilter<String> siteNameFilter;
	private final SingleValuedFilter<String> vendorNameFilter;
	private final SingleValuedFilter<String> siteIdFilter;

	private final EnumTranslator translator;

	@Autowired
	@Qualifier("assetService")
	private Service<Asset, Long, SearchOptions> service;

	public AssetLazyDataModel() {
		translator = new EnumTranslator();

		nameFilter = new SingleValuedFilter<>(NAME_FILTER, RelationalOperator.CONTAINS, "");
		serialNumberFilter = new SingleValuedFilter<>(SERIAL_NUMBER_FILTER, RelationalOperator.CONTAINS, "");
		manufacturerNameFilter = new SingleValuedFilter<>(MANUFACTURER_NAME_FILTER, RelationalOperator.CONTAINS, "");
		assetTypeFilter = new SingleValuedFilter<>(ASSET_TYPE_NAME_FILTER, RelationalOperator.CONTAINS, "");
		manufacturerBusinessImpactFilter = new SingleValuedFilter<>(MANUFACTURER_BUSINESS_IMPACT_FILTER,
				RelationalOperator.CONTAINS, "");
		stateFilter = new SingleValuedFilter<>(ASSET_STATE_FILTER, RelationalOperator.CONTAINS, "");
		acquisitionDateFilter = new SingleValuedFilter<>(ACQUISITION_DATE_FILTER, RelationalOperator.EQUAL, null);
		expiryDateFilter = new SingleValuedFilter<>(EXPIRY_DATE_FILTER, RelationalOperator.EQUAL, null);
		warrantyExpiryDateFilter = new SingleValuedFilter<>(WARRANTY_EXPIRY_DATE_FILTER, RelationalOperator.EQUAL,
				null);
		depreciationMethodFilter = new SingleValuedFilter<>(DEPRECIATION_METHOD, RelationalOperator.CONTAINS, "");
		barCodeFilter = new SingleValuedFilter<>(BARCODE_FILTER, RelationalOperator.CONTAINS, "");
		productNameFilter = new SingleValuedFilter<>(PRODUCT_NAME_FILTER, RelationalOperator.CONTAINS, "");
		siteLocationFilter = new SingleValuedFilter<>(SITE_LOCATION_FILTER, RelationalOperator.CONTAINS, "");
		siteStatusFilter = new SingleValuedFilter<>(SITE_STATUS_FILTER, RelationalOperator.CONTAINS, "");
		siteNameFilter = new SingleValuedFilter<>(SITE_NAME_FILTER, RelationalOperator.CONTAINS, "");
		vendorNameFilter = new SingleValuedFilter<>(VENDOR_NAME_FILTER, RelationalOperator.CONTAINS, "");
		siteIdFilter = new SingleValuedFilter<>(SITE_ID_FILTER);

		filters = new ArrayList<>();
		filters.add(nameFilter);
		filters.add(serialNumberFilter);
		filters.add(manufacturerNameFilter);
		filters.add(manufacturerBusinessImpactFilter);
		filters.add(stateFilter);
		filters.add(acquisitionDateFilter);
		filters.add(expiryDateFilter);
		filters.add(warrantyExpiryDateFilter);
		filters.add(depreciationMethodFilter);
		filters.add(manufacturerNameFilter);
		filters.add(assetTypeFilter);
		filters.add(barCodeFilter);
		filters.add(productNameFilter);
		filters.add(siteLocationFilter);
		filters.add(siteStatusFilter);
		filters.add(siteNameFilter);
		filters.add(vendorNameFilter);
		filters.add(siteIdFilter);
	}

	public SingleValuedFilter<String> getNameFilter() {
		return nameFilter;
	}

	public SingleValuedFilter<String> getSerialNumberFilter() {
		return serialNumberFilter;
	}

	public SingleValuedFilter<String> getManufacturerNameFilter() {
		return manufacturerNameFilter;
	}

	public SingleValuedFilter<String> getAssetTypeFilter() {
		return assetTypeFilter;
	}

	public SingleValuedFilter<String> getStateFilter() {
		return stateFilter;
	}

	public SingleValuedFilter<String> getDepreciationMethodFilter() {
		return depreciationMethodFilter;
	}

	public SingleValuedFilter<String> getSiteLocationFilter() {
		return siteLocationFilter;
	}

	public SingleValuedFilter<String> getManufacturerBusinessImpactFilter() {
		return manufacturerBusinessImpactFilter;
	}

	public SingleValuedFilter<String> getAcquisitionDateFilter() {
		return acquisitionDateFilter;
	}

	public SingleValuedFilter<String> getExpiryDateFilter() {
		return expiryDateFilter;
	}

	public SingleValuedFilter<String> getWarrantyExpiryDateFilter() {
		return warrantyExpiryDateFilter;
	}

	public SingleValuedFilter<String> getBarCodeFilter() {
		return barCodeFilter;
	}

	public SingleValuedFilter<String> getProductNameFilter() {
		return productNameFilter;
	}

	public SingleValuedFilter<String> getSiteStatusFilter() {
		return siteStatusFilter;
	}

	public SingleValuedFilter<String> getSiteNameFilter() {
		return siteNameFilter;
	}

	public SingleValuedFilter<String> getVendorNameFilter() {
		return vendorNameFilter;
	}

	public SingleValuedFilter<String> getSiteIdFilter() {
		return siteIdFilter;
	}

	@Override
	protected List<Filter<?>> getFilters() {
		List<Filter<?>> baseFilters = super.getFilters();
		if (baseFilters != null) {
			ArrayList<Filter<?>> combinedFilters = new ArrayList<>(baseFilters);
			combinedFilters.addAll(filters);
			return combinedFilters;
		}
		return filters;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<Asset> list) {
		super.sort(sortField, sortOrder, list);
		if (sortField == null) {
			sortField = NAME_FILTER; // We want to sort by name if no sort field
										// was specified.
		}
		final SortOrder order = sortOrder;

		if (null != sortField) {
			switch (sortField) {
			case NAME_FILTER: {
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
			case SERIAL_NUMBER_FILTER: {
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
			case MANUFACTURER_NAME_FILTER: {
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

			case ASSET_TYPE_NAME_FILTER: {
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

			case ASSET_STATE_FILTER: {
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
			case MANUFACTURER_BUSINESS_IMPACT_FILTER: {
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
			case ACQUISITION_DATE_FILTER: {
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
			case EXPIRY_DATE_FILTER: {
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
			case WARRANTY_EXPIRY_DATE_FILTER: {
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
			case BARCODE_FILTER: {
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
			case PRODUCT_NAME_FILTER: {
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
			case SITE_NAME_FILTER: {
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
			case SITE_LOCATION_FILTER: {
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
			case SITE_STATUS_FILTER: {
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
			case VENDOR_NAME_FILTER: {
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
			case SITE_ID_FILTER: {
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
	protected Service<Asset, Long, SearchOptions> getService() {
		return service;
	}

}
