package org.rainbow.asset.explorer.service.services;

import java.io.Serializable;
import java.util.List;

import org.rainbow.asset.explorer.orm.entities.Document;
import org.rainbow.service.Service;

public interface DocumentDetailsService<TEntity extends Document, TKey extends Serializable, TSearchOptions extends Object, TDetail extends Object>
		extends Service<TEntity, TKey, TSearchOptions> {

	List<TDetail> getDetails(TKey id) throws Exception;

}