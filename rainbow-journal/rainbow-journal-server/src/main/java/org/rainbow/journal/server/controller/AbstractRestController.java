package org.rainbow.journal.server.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.SearchParam;
import org.rainbow.orm.entities.AbstractAuditableEntity;
import org.rainbow.service.services.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractRestController<TEntity extends AbstractAuditableEntity<TKey>, TKey extends Serializable, TDto, TSearchParam extends SearchParam> {
	protected abstract Service<TEntity> getService();

	protected abstract DtoTranslator<TEntity, TDto> getTranslator();

	protected void setSearchOptionsFilters(SearchOptions options) {
	}

	protected abstract SearchOptionsFactory getSearchOptionsFactory();

	protected abstract Predicate getPredicate(TSearchParam searchParam);

	@RequestMapping(method = RequestMethod.GET)
	public List<TDto> find(@ModelAttribute TSearchParam searchParam) throws Exception {
		checkDependencies();
		
		List<TDto> dtos = new ArrayList<>();
		SearchOptions searchOptions = getSearchOptionsFactory().create(searchParam.getPageIndex(),
				searchParam.getPageSize(), getPredicate(searchParam));
		List<TEntity> entities = getService().find(searchOptions);
		for (TEntity entity : entities)
			dtos.add(getTranslator().toDto(entity));
		return dtos;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<TKey> create(@RequestBody TDto dto) throws Exception {
		checkDependencies();
		
		TEntity entity = getTranslator().fromDto(dto);

		getService().create(entity);

		return new ResponseEntity<TKey>(entity.getId(), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> update(@PathVariable("id") TKey id, @RequestBody TDto dto) throws Exception {
		checkDependencies();

		TEntity entity = getTranslator().fromDto(dto);
		entity.setId(id);

		getService().update(entity);

		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public TDto findById(@PathVariable("id") TKey id) throws Exception {
		checkDependencies();
		return getTranslator().toDto(getService().findById(id));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> delete(@PathVariable("id") TKey id) throws Exception {
		checkDependencies();
		getService().delete(getService().findById(id));
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}

	private void checkDependencies() {
		if (getService() == null) {
			throw new IllegalStateException("The service cannot be null.");
		}
		if (getTranslator() == null) {
			throw new IllegalStateException("The translator cannot be null.");
		}
		if (getSearchOptionsFactory() == null) {
			throw new IllegalStateException("The search options factory cannot be null.");
		}
	}
}
