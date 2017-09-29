package org.rainbow.security.faces.utilities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.rainbow.orm.entities.Identifiable;
import org.rainbow.persistence.ListValuedFilter;
import org.rainbow.persistence.RelationalOperator;
import org.rainbow.persistence.SearchOptions;
import org.rainbow.service.services.Service;

public class ServiceUtil {
	public static <TEntity extends Identifiable<TKey>, TKey extends Serializable & Comparable<? super TKey>> List<TEntity> find(
			Service<TEntity, TKey, SearchOptions> service, List<TKey> ids) throws Exception {
		ListValuedFilter<TKey> filter = new ListValuedFilter<>("id");
		filter.setOperator(RelationalOperator.IN);
		filter.setList(ids);
		SearchOptions options = new SearchOptions();
		options.setFilters(Arrays.asList(filter));
		return service.find(options);
	}
}
