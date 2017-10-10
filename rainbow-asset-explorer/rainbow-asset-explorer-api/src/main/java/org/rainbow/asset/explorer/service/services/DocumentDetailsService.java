package org.rainbow.asset.explorer.service.services;

import java.util.List;

import org.rainbow.asset.explorer.orm.entities.Document;
import org.rainbow.service.services.Service;

public interface DocumentDetailsService<TEntity extends Document, TDetail extends Object> extends Service<TEntity> {

	List<TDetail> getDetails(Object id) throws Exception;

}