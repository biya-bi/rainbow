package org.rainbow.asset.explorer.faces.controllers.read;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.model.SortOrder;
import org.rainbow.asset.explorer.orm.entities.AssetType;
import org.rainbow.asset.explorer.service.services.AssetTypeService;
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
public class AssetTypeReadController extends AbstractNumericIdAuditableEntityReadController<AssetType, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -549020857143976219L;

	private static final String NAME_PATH = "name";

	private final StringSearchCriterion nameSearchCriterion;

	@Autowired
	private AssetTypeService service;

	public AssetTypeReadController() {
		super(Long.class);
		nameSearchCriterion = new StringSearchCriterion(NAME_PATH, StringSearchCriterion.Operator.CONTAINS, null);
	}

	@SearchCriterion
	public StringSearchCriterion getNameSearchCriterion() {
		return nameSearchCriterion;
	}

	@Override
	protected void sort(String sortField, SortOrder sortOrder, List<AssetType> list) {
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
				Collections.sort(list, new Comparator<AssetType>() {
					@Override
					public int compare(AssetType one, AssetType other) {
						int result = comparator.compare(one.getName(), other.getName());
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
	public Service<AssetType> getService() {
		return service;
	}

}
